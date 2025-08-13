// 创建测试数据的脚本
async function createTestData() {
    const API_BASE = 'http://localhost:8080/api';
    
    try {
        console.log('开始创建测试数据...');
        
        const response = await fetch(`${API_BASE}/orders/create-simple-test-data`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const result = await response.json();
            console.log('✅ 测试数据创建成功:', result.msg);
            return true;
        } else {
            const errorText = await response.text();
            console.error('❌ 创建测试数据失败:', response.status, errorText);
            return false;
        }
    } catch (error) {
        console.error('❌ 创建测试数据时发生错误:', error);
        return false;
    }
}

async function testAPI() {
    const API_BASE = 'http://localhost:8080/api';
    
    try {
        console.log('测试餐票订单API...');
        
        const response = await fetch(`${API_BASE}/orders/home-tickets/page?page=0&size=10`);
        
        if (response.ok) {
            const result = await response.json();
            console.log('✅ API测试成功:', result);
            
            if (result.data && result.data.content) {
                console.log(`找到 ${result.data.content.length} 条记录`);
                console.log('数据示例:', result.data.content.slice(0, 2));
            } else {
                console.log('API返回成功但没有数据');
            }
            
            return result;
        } else {
            const errorText = await response.text();
            console.error('❌ API测试失败:', response.status, errorText);
            return null;
        }
    } catch (error) {
        console.error('❌ API测试时发生错误:', error);
        return null;
    }
}

// 主函数
async function main() {
    console.log('=== 餐票订单数据测试 ===');
    
    // 1. 先测试API
    console.log('\n1. 测试API...');
    let result = await testAPI();
    
    // 2. 如果没有数据，创建测试数据
    if (!result || !result.data || !result.data.content || result.data.content.length === 0) {
        console.log('\n2. 没有数据，创建测试数据...');
        const created = await createTestData();
        
        if (created) {
            console.log('\n3. 重新测试API...');
            result = await testAPI();
        }
    } else {
        console.log('\n2. 已有数据，跳过创建步骤');
    }
    
    console.log('\n=== 测试完成 ===');
    return result;
}

// 如果在浏览器环境中运行
if (typeof window !== 'undefined') {
    window.createTestData = createTestData;
    window.testAPI = testAPI;
    window.runTest = main;
    
    // 自动运行测试
    main().then(result => {
        if (result && result.data && result.data.content && result.data.content.length > 0) {
            console.log('🎉 数据准备完成，前端应该可以正常显示数据了！');
        } else {
            console.log('⚠️ 数据准备可能有问题，请检查后端服务');
        }
    });
}

// 如果在Node.js环境中运行
if (typeof module !== 'undefined' && module.exports) {
    module.exports = { createTestData, testAPI, main };
}
