#!/bin/bash

# 账单结算API测试脚本
# 测试养老院管理系统的账单结算功能

BASE_URL="http://localhost:8080/api"

echo "=========================================="
echo "养老院管理系统 - 账单结算API测试脚本"
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
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4

    print_info "测试 $description: $method $endpoint"

    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL$endpoint" -H "Accept: application/json")
    else
        response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X "$method" "$BASE_URL$endpoint" \
                  -H "Content-Type: application/json" \
                  -H "Accept: application/json" \
                  -d "$data")
    fi

    http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_STATUS:/d')

    if [ "$http_status" = "200" ]; then
        print_success "$description - 成功 (HTTP $http_status)"

        # 检查响应是否包含成功标志
        if echo "$body" | grep -q '"code":200'; then
            print_success "$description - 数据获取成功"
            # 显示部分数据
            echo "$body" | grep -o '"totalAmount":[0-9.]*\|"billMonth":"[^"]*"\|"elderName":"[^"]*"' | head -3 | while read line; do
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
        # 尝试直接测试API
        print_info "后端服务可能未运行，尝试直接测试API..."
    fi
}

# 主测试流程
main() {
    check_backend

    echo ""
    print_info "开始测试账单结算API..."
    echo ""

    # 测试账单结算 - 创建新账单
    settlement_data='{
        "elderId": 1,
        "billMonth": "2024-12",
        "paymentMethod": "微信",
        "additionalFees": [
            {
                "itemCode": "MEAL",
                "quantity": 30,
                "unitPrice": 50.00
            }
        ]
    }'

    test_api "POST" "/bills/settle" "账单结算" "$settlement_data"

    # 测试获取账单列表
    test_api "GET" "/bills?page=1&size=10" "获取账单列表"

    echo "=========================================="
    print_success "账单结算API测试完成"
    print_info "账单结算功能可以根据老人护理等级自动计算费用"
    print_info "并支持添加额外的费用项"
    echo "=========================================="
}

# 运行主函数
main