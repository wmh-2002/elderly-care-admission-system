#!/bin/bash

# ===========================================
# 养老院管理系统 - 用户API测试脚本
# 测试所有用户相关的接口
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
TEST_USER_ID=""

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

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
    ((TESTS_PASSED++))
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
    ((TESTS_FAILED++))
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
    ((TESTS_WARNING++))
}

print_info() {
    echo -e "${CYAN}ℹ️  $1${NC}"
}

test_api() {
    local method=$1
    local url=$2
    local data=$3
    local headers=$4
    local description=$5
    local expected_status=${6:-200}

    ((TESTS_TOTAL++))

    echo -e "\n${YELLOW}测试 ${TESTS_TOTAL}: ${description}${NC}"

    # 构建curl命令
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

    # 执行请求
    local response=$(eval $cmd)
    local http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    local body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    echo "响应状态: $http_status"
    if [ -n "$body" ] && [ "$body" != "null" ]; then
        echo "响应内容: $body"
    fi

    # 检查响应
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
        return 0
    else
        print_error "后端服务未运行，请先启动："
        echo "   cd elderly-care-backend && mvn spring-boot:run"
        echo ""
        echo "或者在后台启动："
        echo "   cd elderly-care-backend && nohup mvn spring-boot:run > backend.log 2>&1 &"
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
        # 提取token
        TOKEN=$(echo "$body" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
        ADMIN_ID=$(echo "$body" | grep -o '"id":[0-9]*' | cut -d: -f2)

        if [ -n "$TOKEN" ]; then
            print_success "登录成功，获取到JWT token"
            print_info "Token: ${TOKEN:0:50}..."
            print_info "Admin ID: $ADMIN_ID"
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

# 用户API测试
test_user_apis() {
    print_section "👥 用户管理API测试"

    # 1. 获取用户列表
    test_api "GET" "/api/users" "" "$AUTH_HEADER$TOKEN" "获取用户列表（分页）"

    # 2. 根据ID获取用户信息
    test_api "GET" "/api/users/$ADMIN_ID" "" "$AUTH_HEADER$TOKEN" "根据ID获取用户信息"

    # 3. 创建新用户（使用时间戳确保用户名唯一）
    local timestamp=$(date +%s)
    local unique_username="testuser_${timestamp}"
    local create_user_data="{
        \"username\": \"${unique_username}\",
        \"password\": \"123456\",
        \"realName\": \"测试用户\",
        \"email\": \"test@example.com\",
        \"phone\": \"13800138000\",
        \"roleId\": 2
    }"

    local create_response=$(curl -s -w '\nHTTP_STATUS:%{http_code}' -X POST "$BASE_URL/api/users" \
        -H "Content-Type: application/json" \
        -H "$AUTH_HEADER$TOKEN" \
        -d "$create_user_data")

    local create_status=$(echo "$create_response" | grep "HTTP_STATUS:" | cut -d: -f2)
    local create_body=$(echo "$create_response" | sed '/HTTP_STATUS:/d')

    ((TESTS_TOTAL++))
    echo -e "\n${YELLOW}测试 ${TESTS_TOTAL}: 创建新用户${NC}"
    echo "请求: POST $BASE_URL/api/users"
    echo "数据: $create_user_data"
    echo "响应状态: $create_status"

    if [ "$create_status" -eq 200 ]; then
        print_success "创建用户成功"
        # 提取新创建的用户ID
        TEST_USER_ID=$(echo "$create_body" | grep -o '"id":[0-9]*' | cut -d: -f2 | head -1)
        print_info "新用户ID: $TEST_USER_ID"
    else
        print_error "创建用户失败"
        echo "响应: $create_body"
        # 如果创建失败，尝试查找已存在的测试用户（使用我们尝试创建的用户名）
        TEST_USER_ID=$(curl -s -X GET "$BASE_URL/api/users?page=1&size=20&username=${unique_username}" \
            -H "$AUTH_HEADER$TOKEN" | grep -o '"id":[0-9]*' | cut -d: -f2 | head -1)
        if [ -z "$TEST_USER_ID" ]; then
            # 如果还没找到，尝试查找任何以testuser开头的用户
            TEST_USER_ID=$(curl -s -X GET "$BASE_URL/api/users?page=1&size=20" \
                -H "$AUTH_HEADER$TOKEN" | grep -o '"username":"testuser[^"]*"' | grep -o '"id":[0-9]*' | cut -d: -f2 | head -1)
        fi
    fi

    # 4. 更新用户信息（如果有测试用户）
    if [ -n "$TEST_USER_ID" ]; then
        local update_user_data='{
            "realName": "测试用户（已更新）",
            "email": "updated@example.com",
            "phone": "13900139000"
        }'

        test_api "PUT" "/api/users/$TEST_USER_ID" "$update_user_data" "$AUTH_HEADER$TOKEN" "更新用户信息"

        # 5. 禁用用户
        test_api "PUT" "/api/users/$TEST_USER_ID/disable" "" "$AUTH_HEADER$TOKEN" "禁用用户"

        # 6. 启用用户
        test_api "PUT" "/api/users/$TEST_USER_ID/enable" "" "$AUTH_HEADER$TOKEN" "启用用户"

        # 7. 删除用户（注意：这里只是测试，实际可能不删除）
        # test_api "DELETE" "/api/users/$TEST_USER_ID" "" "$AUTH_HEADER$TOKEN" "删除用户" 200
    fi

    # 8. 测试查询参数
    test_api "GET" "/api/users?page=1&size=5" "" "$AUTH_HEADER$TOKEN" "获取用户列表（指定页码和大小）"

    test_api "GET" "/api/users?page=1&size=10&username=admin" "" "$AUTH_HEADER$TOKEN" "按用户名搜索用户"

    # URL编码中文参数：管理员 -> %E7%AE%A1%E7%90%86%E5%91%98
    test_api "GET" "/api/users?page=1&size=10&realName=%E7%AE%A1%E7%90%86%E5%91%98" "" "$AUTH_HEADER$TOKEN" "按真实姓名搜索用户"
}

# 测试无权限访问
test_unauthorized_access() {
    print_section "🚫 无权限访问测试"

    # 尝试在没有token的情况下访问受保护的API
    test_api "GET" "/api/users" "" "" "无token访问用户列表" 403

    # 使用无效token访问
    test_api "GET" "/api/users" "" "Authorization: Bearer invalid_token" "使用无效token访问用户列表" 403
}

# 打印测试总结
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

# 主函数
main() {
    print_header "🏥 养老院管理系统 - 用户API测试"

    # 检查服务
    check_service

    # 登录获取token
    if ! login_and_get_token; then
        print_error "无法获取JWT token，测试终止"
        exit 1
    fi

    # 测试用户API
    test_user_apis

    # 测试无权限访问
    test_unauthorized_access

    # 打印总结
    print_summary
}

# 运行主函数
main
