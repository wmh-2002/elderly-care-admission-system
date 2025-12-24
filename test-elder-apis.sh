#!/bin/bash

# ===========================================
# 养老院管理系统 - 老人档案API测试脚本
# 测试老人档案的增删改查、筛选和权限控制
# ===========================================

# 配置
BASE_URL="http://localhost:8080"
AUTH_HEADER="Authorization: Bearer "

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 测试计数器
TESTS_TOTAL=0
TESTS_PASSED=0
TESTS_FAILED=0
TESTS_WARNING=0

# 全局变量
TOKEN=""
ADMIN_ID=""
TEST_ELDER_ID=""
TEST_ELDER_NO=""

# 工具函数
print_header() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================================${NC}"
}

print_section() {
    echo -e "${PURPLE}$1${NC}"
    echo -e "${PURPLE}$(printf '%.0s=' {1..50})${NC}"
}

print_success() { echo -e "${GREEN}✅ $1${NC}"; ((TESTS_PASSED++)); }
print_error()   { echo -e "${RED}❌ $1${NC}"; ((TESTS_FAILED++)); }
print_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; ((TESTS_WARNING++)); }
print_info()    { echo -e "${CYAN}ℹ️  $1${NC}"; }

test_api() {
    local method=$1
    local url=$2
    local data=$3
    local headers=$4
    local description=$5
    local expected_status=${6:-200}

    ((TESTS_TOTAL++))
    echo -e "\n${YELLOW}测试 ${TESTS_TOTAL}: ${description}${NC}"

    local cmd="curl -s -w '\\nHTTP_STATUS:%{http_code}' -X $method \"$BASE_URL$url\""
    if [ -n "$data" ]; then
        cmd="$cmd -H \"Content-Type: application/json\" -d '$data'"
    fi
    if [ -n "$headers" ]; then
        cmd="$cmd -H \"$headers\""
    fi

    echo "请求: $method $BASE_URL$url"
    if [ -n "$data" ] && [ "$data" != "null" ]; then
        echo "数据: $data"
    fi

    local response=$(eval $cmd)
    local http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    local body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    echo "响应状态: $http_status"
    if [ -n "$body" ] && [ "$body" != "null" ]; then
        echo "响应内容: $body"
    fi

    if [ "$http_status" -eq "$expected_status" ]; then
        print_success "测试通过"
        return 0
    else
        print_error "测试失败 - 期望状态 $expected_status，实际状态 $http_status"
        return 1
    fi
}

# 检查服务是否运行
check_service() {
    print_info "检查后端服务状态..."
    if curl -s --max-time 3 http://localhost:8080/ > /dev/null 2>&1; then
        print_success "后端服务正在运行"
    else
        print_error "后端服务未运行，请先启动："
        echo "   cd elderly-care-backend && mvn spring-boot:run"
        exit 1
    fi
}

# 登录获取token
login_and_get_token() {
    print_section "🔐 用户认证测试"
    print_info "正在登录获取JWT token..."

    local response=$(curl -s -w '\nHTTP_STATUS:%{http_code}' -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"123456"}')

    local http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    local body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    if [ "$http_status" -eq 200 ]; then
        TOKEN=$(echo "$body" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
        ADMIN_ID=$(echo "$body" | grep -o '"id":[0-9]*' | cut -d: -f2)
        if [ -n "$TOKEN" ]; then
            print_success "登录成功，获取到JWT token"
            print_info "Token: ${TOKEN:0:50}..."
            return 0
        else
            print_error "登录响应中未找到token"
            return 1
        fi
    else
        print_error "登录失败 - HTTP状态: $http_status"
        echo "响应: $body"
        return 1
    fi
}

# 老人档案API测试
test_elder_apis() {
    print_section "🧓 老人档案API测试"

    # 1. 获取老人列表（分页）
    test_api "GET" "/api/elders" "" "$AUTH_HEADER$TOKEN" "获取老人列表（分页）"

    # 生成唯一编号/身份证
    local ts=$(date +%s)
    TEST_ELDER_NO="E${ts}"
    local id_card="11010119700101${ts: -4}"

    # 2. 创建新老人档案
    local create_elder_data="{
        \"elderNo\": \"${TEST_ELDER_NO}\",
        \"name\": \"测试老人\",
        \"gender\": \"M\",
        \"birthDate\": \"1950-01-01\",
        \"idCard\": \"${id_card}\",
        \"phone\": \"1380000${ts: -4}\",
        \"address\": \"测试地址\",
        \"contactName\": \"家属\",
        \"contactPhone\": \"1390000${ts: -4}\",
        \"contactRelation\": \"子女\",
        \"bedId\": 1,
        \"careLevel\": \"L1\",
        \"feeStandard\": 30.00,
        \"status\": 1
    }"

    local create_response=$(curl -s -w '\nHTTP_STATUS:%{http_code}' -X POST "$BASE_URL/api/elders" \
        -H "Content-Type: application/json" \
        -H "$AUTH_HEADER$TOKEN" \
        -d "$create_elder_data")

    local create_status=$(echo "$create_response" | grep "HTTP_STATUS:" | cut -d: -f2)
    local create_body=$(echo "$create_response" | sed '/HTTP_STATUS:/d')

    ((TESTS_TOTAL++))
    echo -e "\n${YELLOW}测试 ${TESTS_TOTAL}: 创建老人档案${NC}"
    echo "请求: POST $BASE_URL/api/elders"
    echo "数据: $create_elder_data"
    echo "响应状态: $create_status"

    if [ "$create_status" -eq 200 ]; then
        print_success "创建老人档案成功"
        TEST_ELDER_ID=$(echo "$create_body" | grep -o '"id":[0-9]*' | cut -d: -f2 | head -1)
        print_info "新老人ID: $TEST_ELDER_ID, 编号: $TEST_ELDER_NO"
    else
        print_error "创建老人档案失败"
        echo "响应: $create_body"
    fi

    # 3. 根据ID获取
    if [ -n "$TEST_ELDER_ID" ]; then
        test_api "GET" "/api/elders/$TEST_ELDER_ID" "" "$AUTH_HEADER$TOKEN" "根据ID获取老人信息"
    fi

    # 4. 更新老人档案（更换护理等级并退住）
    if [ -n "$TEST_ELDER_ID" ]; then
        local update_elder_data='{
            "name": "更新的测试老人",
            "careLevel": "L2",
            "feeStandard": 60.00,
            "status": 0
        }'
        test_api "PUT" "/api/elders/$TEST_ELDER_ID" "$update_elder_data" "$AUTH_HEADER$TOKEN" "更新老人档案并退住"
    fi

    # 5. 查询筛选
    test_api "GET" "/api/elders?page=1&size=5&elderNo=${TEST_ELDER_NO}" "" "$AUTH_HEADER$TOKEN" "按老人编号搜索"
    # URL编码中文：测试 -> %E6%B5%8B%E8%AF%95
    test_api "GET" "/api/elders?page=1&size=5&name=%E6%B5%8B%E8%AF%95" "" "$AUTH_HEADER$TOKEN" "按姓名搜索"
    test_api "GET" "/api/elders?page=1&size=5&status=0" "" "$AUTH_HEADER$TOKEN" "按状态筛选（退住）"
    test_api "GET" "/api/elders?page=1&size=5&careLevel=L1" "" "$AUTH_HEADER$TOKEN" "按护理等级筛选"

    # 6. 删除老人档案（可选，如需保留可注释掉）
    # if [ -n "$TEST_ELDER_ID" ]; then
    #     test_api "DELETE" "/api/elders/$TEST_ELDER_ID" "" "$AUTH_HEADER$TOKEN" "删除老人档案" 200
    # fi
}

# 无权限访问测试
test_unauthorized_access() {
    print_section "🚫 无权限访问测试"
    test_api "GET" "/api/elders" "" "" "无token访问老人列表" 403
    test_api "GET" "/api/elders" "" "Authorization: Bearer invalid_token" "无效token访问老人列表" 403
}

print_summary() {
    print_header "📊 测试总结"
    echo -e "${CYAN}总测试数: $TESTS_TOTAL${NC}"
    echo -e "${GREEN}通过: $TESTS_PASSED${NC}"
    echo -e "${RED}失败: $TESTS_FAILED${NC}"
    echo -e "${YELLOW}警告: $TESTS_WARNING${NC}"
    local success_rate=$((TESTS_PASSED * 100 / TESTS_TOTAL))
    echo -e "${BLUE}成功率: ${success_rate}%${NC}"
    if [ $TESTS_FAILED -eq 0 ]; then
        echo -e "${GREEN}🎉 所有测试通过！${NC}"
    else
        echo -e "${RED}⚠️  有 $TESTS_FAILED 个测试失败${NC}"
    fi
}

main() {
    print_header "🏥 养老院管理系统 - 老人档案API测试"
    check_service
    if ! login_and_get_token; then
        print_error "无法获取JWT token，测试终止"
        exit 1
    fi
    test_elder_apis
    test_unauthorized_access
    print_summary
}

main

