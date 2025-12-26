#!/bin/bash

# 前端账单结算功能测试脚本
# 测试养老院管理系统前端账单结算页面

echo "=========================================="
echo "养老院管理系统 - 前端账单结算测试脚本"
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

print_warning() {
    echo -e "\033[33m⚠️  $1\033[0m"
}

# 检查前端服务是否运行
check_frontend() {
    print_info "检查前端服务状态..."

    if curl -s "http://localhost:5173" > /dev/null 2>&1; then
        print_success "前端服务正在运行 (http://localhost:5173)"
    else
        print_warning "前端服务可能未运行，请先启动前端服务："
        print_info "  cd elderly-care-frontend && npm run dev"
        echo ""
    fi
}

# 检查后端服务是否运行
check_backend() {
    print_info "检查后端服务状态..."

    if curl -s "http://localhost:8080/api/health" > /dev/null 2>&1 || curl -s "http://localhost:8080/api" > /dev/null 2>&1; then
        print_success "后端服务正在运行"
    else
        print_warning "后端服务可能未运行，请先启动后端服务："
        print_info "  cd elderly-care-backend && mvn spring-boot:run"
        echo ""
    fi
}

# 测试账单API
test_bill_apis() {
    print_info "测试账单相关API..."

    # 测试获取账单列表
    if curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/bills?page=1&size=10" | grep -q "200"; then
        print_success "账单列表API正常"
    else
        print_error "账单列表API异常"
    fi

    # 测试获取老人列表（用于下拉选择）
    if curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/api/elders?page=1&size=1000" | grep -q "200"; then
        print_success "老人列表API正常"
    else
        print_error "老人列表API异常"
    fi
}

# 显示使用说明
show_usage() {
    echo ""
    print_info "前端账单结算功能使用说明："
    echo ""
    print_info "1. 访问费用结算页面："
    print_info "   http://localhost:5173/#/fee-settlement"
    echo ""
    print_info "2. 功能说明："
    print_info "   • 查看账单列表：显示所有账单记录"
    print_info "   • 搜索筛选：按老人、月份、状态筛选账单"
    print_info "   • 新增结算：为老人自动生成账单"
    print_info "   • 账单支付：支付未缴清的账单"
    print_info "   • 查看详情：查看账单明细信息"
    echo ""
    print_info "3. 账单结算流程："
    print_info "   选择老人 → 选择月份 → 系统自动计算费用 → 生成账单"
    echo ""
    print_info "4. 费用计算规则："
    print_info "   • 护理费用 = 护理等级日单价 × 当月天数"
    print_info "   • 支持添加额外费用项目"
    print_info "   • 支付方式：现金、微信、支付宝、银行卡、其他"
    echo ""
}

# 主测试流程
main() {
    echo ""
    check_frontend
    check_backend

    echo ""
    test_bill_apis

    echo ""
    show_usage

    echo "=========================================="
    print_success "前端账单结算功能测试完成"
    print_info "请在浏览器中访问费用结算页面进行手动测试"
    echo "=========================================="
}

# 运行主函数
main
