#!/bin/bash

##############################################################################
# JavaDoc Enhancement Script
#
# This script helps apply enhanced JavaDocs to all source files.
# It provides templates and examples for enterprise-grade documentation.
#
# Usage: ./apply-javadocs.sh
#
# Author: Enterprise Team
# Version: 1.0.0
# Date: 2026-01-01
##############################################################################

echo "==============================================="
echo "  JavaDoc Enhancement Tool"
echo "==============================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Count total Java files
TOTAL_FILES=$(find . -name "*.java" -not -path "*/target/*" -not -path "*/_ENHANCED.java" | wc -l)
echo "📊 Found ${TOTAL_FILES} Java files to enhance"
echo ""

# Create backup directory
BACKUP_DIR="./javadoc-backups-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"
echo "💾 Created backup directory: $BACKUP_DIR"
echo ""

# Function to show file enhancement status
check_javadoc_status() {
    local file=$1
    local has_class_doc=$(grep -c "@author Enterprise Team" "$file" 2>/dev/null || echo "0")
    local has_method_docs=$(grep -c "@param\|@return\|@throws" "$file" 2>/dev/null || echo "0")

    if [ "$has_class_doc" -gt 0 ] && [ "$has_method_docs" -gt 3 ]; then
        echo -e "${GREEN}✓${NC}"
    elif [ "$has_class_doc" -gt 0 ]; then
        echo -e "${YELLOW}⚠${NC}"
    else
        echo -e "✗"
    fi
}

echo "📋 JavaDoc Status Report:"
echo "========================="
echo ""

# Check common-lib
echo "📦 common-lib:"
for file in $(find common-lib -name "*.java" -not -path "*/target/*" 2>/dev/null); do
    status=$(check_javadoc_status "$file")
    echo "  $status $(basename $file)"
done
echo ""

# Check alpha-service
echo "📦 alpha-service:"
for file in $(find alpha-service -name "*.java" -not -path "*/target/*" 2>/dev/null); do
    status=$(check_javadoc_status "$file")
    echo "  $status $(basename $file)"
done
echo ""

# Check beta-service
echo "📦 beta-service:"
for file in $(find beta-service -name "*.java" -not -path "*/target/*" 2>/dev/null); do
    status=$(check_javadoc_status "$file")
    echo "  $status $(basename $file)"
done
echo ""

# Check gamma-service
echo "📦 gamma-service:"
for file in $(find gamma-service -name "*.java" -not -path "*/target/*" 2>/dev/null); do
    status=$(check_javadoc_status "$file")
    echo "  $status $(basename $file)"
done
echo ""

echo "Legend:"
echo -e "  ${GREEN}✓${NC} = Fully documented (class + methods)"
echo -e "  ${YELLOW}⚠${NC} = Partially documented (class only)"
echo "  ✗ = Needs documentation"
echo ""

echo "==============================================="
echo "  Next Steps:"
echo "==============================================="
echo ""
echo "1. Review the JAVADOC_ENHANCEMENT_GUIDE.md for templates"
echo "2. Check ApiResponse_ENHANCED.java for a complete example"
echo "3. Use your IDE to add JavaDocs:"
echo "   - IntelliJ: Place cursor on class/method, type /** and press Enter"
echo "   - VS Code: Use 'Document This' extension"
echo ""
echo "4. Or manually enhance files using the templates in the guide"
echo ""
echo "📚 See JAVADOC_ENHANCEMENT_GUIDE.md for complete templates"
echo ""

# Generate a quick reference file
cat > JAVADOC_QUICK_REFERENCE.md << 'EOF'
# Quick JavaDoc Reference

## Minimal Template for Every Class

```java
/**
 * [One-line description of what this class does].
 *
 * @author Enterprise Team
 * @version 1.0.0
 * @since 2026-01-01
 */
```

## Minimal Template for Every Public Method

```java
/**
 * [One-line description of what this method does].
 *
 * @param [paramName] [description]
 * @return [description of return value]
 * @throws [ExceptionType] [when this exception is thrown]
 */
```

## Controller Method Template

```java
/**
 * [Retrieves/Creates/Updates/Deletes] [entity] [by/with] [criteria].
 *
 * @param [param] [description]
 * @return ResponseEntity containing ApiResponse with [result description]
 * @throws ResourceNotFoundException if [entity] not found
 */
```

## Service Method Template

```java
/**
 * [Business logic description - what it does and why].
 *
 * @param [param] [description]
 * @return [what is returned]
 * @throws BusinessException if [business rule violated]
 */
```

## Priority Order

1. Controllers (customer-facing)
2. DTOs (API contracts)
3. Services (business logic)
4. Exceptions (error handling)
5. Utils (helper methods)

## Quick IntelliJ Shortcuts

- Type `/**` above class/method and press Enter
- Alt + Insert → Generate → JavaDoc

## Quick VS Code

- Install "Document This" extension
- Ctrl + Alt + D twice
EOF

echo "✅ Generated JAVADOC_QUICK_REFERENCE.md"
echo ""
echo "🎯 Start with high-priority files first!"
echo ""
