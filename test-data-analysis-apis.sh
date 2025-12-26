#!/bin/bash

# 数据分析API测试脚本
# 测试养老院管理系统的数据分析功能

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "养老院管理系统 - 数据分析API测试脚本"
echo "=========================================="

# 颜色输出函数
print_success() {
    echo -e "\033[32m✅ $1\033[0m"
}

print_error() {
    echo -e "\033[31m❌ $1\033[0m"
}

print_info() {
    echo -e "\033[34mℹ️  $1\033[0m"
}

# 测试函数
test_api() {
    local endpoint=$1
    local description=$2

    print_info "测试 $description: $endpoint"

    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL$endpoint" -H "Accept: application/json")

    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    if [ "$http_status" = "200" ]; then
        print_success "$description - 成功 (HTTP $http_status)"

        # 检查响应是否包含成功标志或数据
        if echo "$body" | grep -q '"code":200'; then
            print_success "$description - 数据获取成功"
            # 显示部分数据
            echo "$body" | grep -o '"totalElders":[0-9]*\|"occupiedBeds":[0-9]*\|"monthlyRevenue":[0-9]*' | head -3 | while read line; do
                print_info "  $line"
            done
        else
            print_info "$description - 响应: ${body:0:100}..."
        fi
    else
        print_error "$description - 失败 (HTTP $http_status)"
        print_error "响应: ${body:0:200}"
    fi

    echo ""
}

# 检查后端服务是否运行
check_backend() {
    print_info "检查后端服务状态..."

    if curl -s "$BASE_URL/health" > /dev/null 2>&1; then
        print_success "后端服务正在运行"
    else
        # 尝试直接测试数据分析API
        print_info "后端服务可能未运行，尝试直接测试数据分析API..."
    fi
}

# 主测试流程
main() {
    check_backend

    echo ""
    print_info "开始测试数据分析API..."
    echo ""

    # 测试各个数据分析端点
    test_api "/api/data-analysis/elder" "老人分析数据"
    test_api "/api/data-analysis/revenue" "营收分析数据"
    test_api "/api/data-analysis/operation" "运营分析数据"
    test_api "/api/data-analysis" "完整数据分析"

    echo "=========================================="
    print_success "数据分析API测试完成"
    print_info "如果所有测试都通过，说明数据分析功能正常工作"
    echo "=========================================="
}

# 运行主函数
main
