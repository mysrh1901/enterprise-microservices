pipeline {
    agent any

    parameters {
        booleanParam(name: 'DEPLOY_ALPHA', defaultValue: true, description: 'Deploy Alpha Service')
        booleanParam(name: 'DEPLOY_BETA', defaultValue: true, description: 'Deploy Beta Service')
        booleanParam(name: 'DEPLOY_GAMMA', defaultValue: true, description: 'Deploy Gamma Service')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'production'], description: 'Target environment')
        string(name: 'IMAGE_TAG', defaultValue: '', description: 'Docker image tag (defaults to BUILD_NUMBER)')
    }

    environment {
        DOCKER_REGISTRY = 'enterprise'
        IMAGE_TAG = "${params.IMAGE_TAG ?: env.BUILD_NUMBER}"
        KUBE_NAMESPACE = "${params.ENVIRONMENT == 'production' ? 'prod' : params.ENVIRONMENT}"
    }

    tools {
        jdk 'JDK17'
    }

    stages {
        stage('🔍 Checkout') {
            steps {
                checkout scm
                script {
                    echo "Building commit: ${env.GIT_COMMIT}"
                    echo "Branch: ${env.GIT_BRANCH}"
                }
            }
        }

        stage('📚 Build Common Library') {
            when {
                anyOf {
                    expression { params.DEPLOY_ALPHA }
                    expression { params.DEPLOY_BETA }
                    expression { params.DEPLOY_GAMMA }
                }
            }
            steps {
                sh './gradlew :common-lib:build'
            }
        }

        stage('🔨 Build Services') {
            parallel {
                stage('👤 Alpha Service') {
                    when {
                        expression { params.DEPLOY_ALPHA }
                    }
                    stages {
                        stage('Build') {
                            steps {
                                sh './gradlew :alpha-service:clean :alpha-service:build'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh './gradlew :alpha-service:test'
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: 'alpha-service/build/test-results/test/*.xml'
                                }
                            }
                        }
                    }
                }

                stage('📦 Beta Service') {
                    when {
                        expression { params.DEPLOY_BETA }
                    }
                    stages {
                        stage('Build') {
                            steps {
                                sh './gradlew :beta-service:clean :beta-service:build'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh './gradlew :beta-service:test'
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: 'beta-service/build/test-results/test/*.xml'
                                }
                            }
                        }
                    }
                }

                stage('📊 Gamma Service') {
                    when {
                        expression { params.DEPLOY_GAMMA }
                    }
                    stages {
                        stage('Build') {
                            steps {
                                sh './gradlew :gamma-service:clean :gamma-service:build'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh './gradlew :gamma-service:test'
                            }
                            post {
                                always {
                                    junit allowEmptyResults: true, testResults: 'gamma-service/build/test-results/test/*.xml'
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('🐳 Build Docker Images') {
            parallel {
                stage('👤 Alpha Image') {
                    when {
                        expression { params.DEPLOY_ALPHA }
                    }
                    steps {
                        script {
                            docker.build("${DOCKER_REGISTRY}/alpha-service:${IMAGE_TAG}", "-f alpha-service/Dockerfile .")
                        }
                    }
                }

                stage('📦 Beta Image') {
                    when {
                        expression { params.DEPLOY_BETA }
                    }
                    steps {
                        script {
                            docker.build("${DOCKER_REGISTRY}/beta-service:${IMAGE_TAG}", "-f beta-service/Dockerfile .")
                        }
                    }
                }

                stage('📊 Gamma Image') {
                    when {
                        expression { params.DEPLOY_GAMMA }
                    }
                    steps {
                        script {
                            docker.build("${DOCKER_REGISTRY}/gamma-service:${IMAGE_TAG}", "-f gamma-service/Dockerfile .")
                        }
                    }
                }
            }
        }

        stage('📤 Push Docker Images') {
            when {
                expression { params.ENVIRONMENT != 'dev' }
            }
            parallel {
                stage('👤 Push Alpha') {
                    when {
                        expression { params.DEPLOY_ALPHA }
                    }
                    steps {
                        script {
                            docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                                docker.image("${DOCKER_REGISTRY}/alpha-service:${IMAGE_TAG}").push()
                                docker.image("${DOCKER_REGISTRY}/alpha-service:${IMAGE_TAG}").push('latest')
                            }
                        }
                    }
                }

                stage('📦 Push Beta') {
                    when {
                        expression { params.DEPLOY_BETA }
                    }
                    steps {
                        script {
                            docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                                docker.image("${DOCKER_REGISTRY}/beta-service:${IMAGE_TAG}").push()
                                docker.image("${DOCKER_REGISTRY}/beta-service:${IMAGE_TAG}").push('latest')
                            }
                        }
                    }
                }

                stage('📊 Push Gamma') {
                    when {
                        expression { params.DEPLOY_GAMMA }
                    }
                    steps {
                        script {
                            docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                                docker.image("${DOCKER_REGISTRY}/gamma-service:${IMAGE_TAG}").push()
                                docker.image("${DOCKER_REGISTRY}/gamma-service:${IMAGE_TAG}").push('latest')
                            }
                        }
                    }
                }
            }
        }

        stage('☸️ Deploy to Kubernetes') {
            when {
                expression { params.ENVIRONMENT != 'dev' }
            }
            parallel {
                stage('👤 Deploy Alpha') {
                    when {
                        expression { params.DEPLOY_ALPHA }
                    }
                    steps {
                        script {
                            sh """
                                kubectl set image deployment/alpha-service \
                                    alpha-service=${DOCKER_REGISTRY}/alpha-service:${IMAGE_TAG} \
                                    -n ${KUBE_NAMESPACE}
                                kubectl rollout status deployment/alpha-service -n ${KUBE_NAMESPACE} --timeout=300s
                            """
                        }
                    }
                }

                stage('📦 Deploy Beta') {
                    when {
                        expression { params.DEPLOY_BETA }
                    }
                    steps {
                        script {
                            sh """
                                kubectl set image deployment/beta-service \
                                    beta-service=${DOCKER_REGISTRY}/beta-service:${IMAGE_TAG} \
                                    -n ${KUBE_NAMESPACE}
                                kubectl rollout status deployment/beta-service -n ${KUBE_NAMESPACE} --timeout=300s
                            """
                        }
                    }
                }

                stage('📊 Deploy Gamma') {
                    when {
                        expression { params.DEPLOY_GAMMA }
                    }
                    steps {
                        script {
                            sh """
                                kubectl set image deployment/gamma-service \
                                    gamma-service=${DOCKER_REGISTRY}/gamma-service:${IMAGE_TAG} \
                                    -n ${KUBE_NAMESPACE}
                                kubectl rollout status deployment/gamma-service -n ${KUBE_NAMESPACE} --timeout=300s
                            """
                        }
                    }
                }
            }
        }

        stage('💚 Smoke Tests') {
            when {
                expression { params.ENVIRONMENT != 'dev' }
            }
            steps {
                script {
                    def services = []
                    if (params.DEPLOY_ALPHA) services.add(['name': 'alpha-service', 'port': '8081', 'health': '/api/users/health'])
                    if (params.DEPLOY_BETA) {
                        services.add(['name': 'beta-service-v1', 'port': '8082', 'health': '/api/v1/orders/health'])
                        services.add(['name': 'beta-service-v2', 'port': '8082', 'health': '/api/v2/orders/health'])
                    }
                    if (params.DEPLOY_GAMMA) services.add(['name': 'gamma-service', 'port': '8083', 'health': '/api/inventory/health'])

                    services.each { svc ->
                        def serviceUrl = sh(
                            script: "kubectl get svc ${svc.name.split('-v')[0]} -n ${KUBE_NAMESPACE} -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo 'localhost'",
                            returnStdout: true
                        ).trim()

                        sh """
                            echo "Testing ${svc.name}..."
                            curl -f -s --retry 5 --retry-delay 10 http://${serviceUrl}:${svc.port}${svc.health} || exit 1
                            echo "✅ ${svc.name} is healthy"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                def deployedServices = []
                if (params.DEPLOY_ALPHA) deployedServices.add('Alpha')
                if (params.DEPLOY_BETA) deployedServices.add('Beta')
                if (params.DEPLOY_GAMMA) deployedServices.add('Gamma')

                echo """
                ✅ Pipeline completed successfully!

                📋 Summary:
                - Environment: ${params.ENVIRONMENT}
                - Image Tag: ${IMAGE_TAG}
                - Services Deployed: ${deployedServices.join(', ')}
                """
            }
        }
        failure {
            script {
                echo """
                ❌ Pipeline failed!

                Please check the logs for details.
                """
            }
        }
        always {
            cleanWs()
        }
    }
}
