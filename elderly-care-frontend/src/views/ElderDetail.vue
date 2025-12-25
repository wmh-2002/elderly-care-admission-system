<template>
  <div class="elder-detail-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/elders' }">老人档案管理</el-breadcrumb-item>
      <el-breadcrumb-item>老人详情</el-breadcrumb-item>
    </el-breadcrumb>
    <el-card class="elder-info-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">老人详情</span>
          <div class="card-operation">
            <el-button @click="goBack" :icon="Back">返回</el-button>
            <el-button type="primary" @click="editElder" :icon="Edit">编辑</el-button>
          </div>
        </div>
      </template>
      
      <!-- 老人基本信息 -->
      <el-descriptions :column="3" border>
        <el-descriptions-item label="姓名" width="100px">
          <span class="info-value">{{ elderDetail.name }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="老人编号" width="120px">
          <span class="info-value">{{ elderDetail.elderNo }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="性别" width="80px">
          <span class="info-value">{{ elderDetail.gender === 'M' ? '男' : elderDetail.gender === 'F' ? '女' : '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="年龄" width="80px">
          <span class="info-value">{{ elderDetail.age }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="出生日期" width="120px">
          <span class="info-value">{{ elderDetail.birthDate || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="身份证号" width="180px">
          <span class="info-value">{{ elderDetail.idCard || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="联系电话" width="150px">
          <span class="info-value">{{ elderDetail.phone || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="房间号" width="100px">
          <span class="info-value">{{ elderDetail.roomNo || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="床号" width="80px">
          <span class="info-value">{{ elderDetail.bedNo || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="护理等级" width="120px">
          <el-tag :type="getLevelType(elderDetail.careLevel)">
            {{ formatCareLevel(elderDetail.careLevel) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态" width="100px">
          <el-tag :type="elderDetail.status === 1 ? 'success' : 'info'">
            {{ elderDetail.status === 1 ? '在院' : '退住' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入住日期" width="120px" :span="2">
          <span class="info-value">{{ elderDetail.checkinDate || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="家庭地址" :span="3">
          <span class="info-value">{{ elderDetail.address || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">
          <span class="info-value">{{ elderDetail.remarks || '-' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 档案信息 -->
    <el-card class="archive-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">档案信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="紧急联系人">
          <span class="info-value">{{ elderDetail.emergencyContact || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">
          <span class="info-value">{{ elderDetail.emergencyPhone || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">
          <span class="info-value">{{ elderDetail.allergyHistory || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="病史" :span="2">
          <span class="info-value">{{ elderDetail.medicalHistory || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="特殊需求" :span="2">
          <span class="info-value">{{ elderDetail.specialNeeds || '-' }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- 护理信息 -->
    <el-card class="nursing-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">护理信息</span>
        </div>
      </template>
      <div class="nursing-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="当前护理计划">
            <span class="info-value">{{ currentNursingPlan.planName || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="护理分类">
            <span class="info-value">{{ currentNursingPlan.categoryName || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">
            <span class="info-value">{{ currentNursingPlan.startDate || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="预计结束日期">
            <span class="info-value">{{ currentNursingPlan.endDate || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="护理员">
            <span class="info-value">{{ currentNursingPlan.assignedNurse || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="计划状态">
            <el-tag :type="getStatusType(currentNursingPlan.status)">
              {{ currentNursingPlan.status || '-' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="nursing-plan-description" v-if="currentNursingPlan.description">
          <h4>护理计划详情：</h4>
          <p>{{ currentNursingPlan.description }}</p>
        </div>
      </div>
    </el-card>
    
    <!-- 历史病例 -->
    <el-card class="medical-history-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">历史病例</span>
          <el-button type="primary" :icon="Plus" @click="addMedicalRecord">新增病例</el-button>
        </div>
      </template>
      <div v-if="medicalHistoryList.length > 0">
        <el-timeline>
          <el-timeline-item 
            v-for="record in medicalHistoryList" 
            :key="record.id"
            :timestamp="record.recordDate"
            :type="record.severity === 'high' ? 'danger' : record.severity === 'medium' ? 'warning' : 'primary'"
          >
            <el-card class="medical-record-card">
              <h4>{{ record.title }}</h4>
              <p><strong>诊断：</strong>{{ record.diagnosis }}</p>
              <p><strong>症状：</strong>{{ record.symptoms }}</p>
              <p><strong>治疗方案：</strong>{{ record.treatmentPlan }}</p>
              <p><strong>医生：</strong>{{ record.doctor }}</p>
              <div class="medical-actions">
                <el-button size="small" @click="viewMedicalDetail(record)">查看详情</el-button>
                <el-button size="small" type="primary" @click="editMedicalRecord(record)">编辑</el-button>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
      <div v-else class="no-medical-records">
        <el-empty description="暂无历史病例" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  Back, 
  Edit, 
  Plus 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 老人详细信息
const elderDetail = ref({
  id: 1,
  name: '老人姓名',
  elderNo: 'ELDER0001',
  gender: 'M',
  age: 75,
  birthDate: '1948-05-15',
  idCard: '123456789012345678',
  phone: '13800138000',
  roomNo: '101',
  bedNo: '1',
  careLevel: '2级护理',
  status: 1,
  checkinDate: '2023-03-15',
  address: '北京市朝阳区某某街道某某号',
  remarks: '备注信息',
  emergencyContact: '张三',
  emergencyPhone: '13900139000',
  allergyHistory: '青霉素过敏',
  medicalHistory: '高血压、糖尿病',
  specialNeeds: '需要定期测量血压'
})

// 当前护理计划
const currentNursingPlan = ref({
  planName: '日常护理计划',
  categoryName: '二级护理',
  startDate: '2023-03-15',
  endDate: '2024-03-15',
  assignedNurse: '李护理员',
  status: '进行中',
  description: '按照二级护理标准，每日定时测量生命体征，协助老人进行日常活动，注意饮食营养搭配。'
})

// 历史病例列表
const medicalHistoryList = ref([
  {
    id: 1,
    title: '高血压复查',
    diagnosis: '原发性高血压',
    symptoms: '头晕、头痛',
    treatmentPlan: '继续服用降压药物，定期监测血压',
    doctor: '王医生',
    recordDate: '2024-01-15',
    severity: 'medium'
  },
  {
    id: 2,
    title: '糖尿病检查',
    diagnosis: '2型糖尿病',
    symptoms: '血糖偏高',
    treatmentPlan: '调整用药方案，控制饮食',
    doctor: '李医生',
    recordDate: '2023-11-20',
    severity: 'high'
  }
])

// 获取等级类型
const getLevelType = (level) => {
  switch(level) {
    case '1级护理': return 'primary'
    case '2级护理': return 'success'
    case '3级护理': return 'warning'
    case '特级护理': return 'danger'
    default: return 'info'
  }
}

// 格式化护理等级显示
const formatCareLevel = (level) => {
  switch(level) {
    case '1级护理': return '一级护理'
    case '2级护理': return '二级护理'
    case '3级护理': return '三级护理'
    case '特级护理': return '特级护理'
    default: return level
  }
}

// 获取状态类型
const getStatusType = (status) => {
  switch(status) {
    case '进行中': return 'primary'
    case '已完成': return 'success'
    case '已取消': return 'danger'
    default: return 'info'
  }
}

// 返回按钮
const goBack = () => {
  router.go(-1)
}

// 编辑老人信息
const editElder = () => {
  // 跳转到编辑页面
  router.push(`/elders/edit/${elderDetail.value.id}`)
}

// 新增病例
const addMedicalRecord = () => {
  // 跳转到新增病例页面
  router.push(`/elders/${elderDetail.value.id}/medical-record/new`)
}

// 查看病例详情
const viewMedicalDetail = (record) => {
  console.log('View medical detail:', record)
}

// 编辑病例
const editMedicalRecord = (record) => {
  console.log('Edit medical record:', record)
  // 跳转到编辑病例页面
  router.push(`/elders/${elderDetail.value.id}/medical-record/${record.id}/edit`)
}

// 模拟加载老人详细信息
const loadElderDetail = () => {
  // 实际应用中会从API获取老人详细信息
  const elderId = route.params.id  // 假设有ID参数
  console.log('Loading elder detail for ID:', elderId)
  
  // 模拟数据加载
  elderDetail.value = {
    id: elderId || 1,
    name: `老人${elderId || 1}`,
    elderNo: `ELDER${String(elderId || 1).padStart(4, '0')}`,
    gender: Math.random() > 0.5 ? 'M' : 'F',
    age: 70 + Math.floor(Math.random() * 30),
    birthDate: `${1950 + Math.floor(Math.random() * 30)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
    idCard: `123456789012345${String(Math.floor(Math.random() * 9999)).padStart(4, '0')}`,
    phone: `138${Math.floor(10000000 + Math.random() * 90000000)}`,
    roomNo: `${Math.floor(Math.random() * 10) + 1}${String(Math.floor(Math.random() * 100)).padStart(2, '0')}`,
    bedNo: String(Math.floor(Math.random() * 10) + 1),
    careLevel: ['1级护理', '2级护理', '3级护理'][Math.floor(Math.random() * 3)],
    status: Math.random() > 0.2 ? 1 : 0,
    checkinDate: `${2020 + Math.floor(Math.random() * 4)}-${String(Math.floor(Math.random() * 12) + 1).padStart(2, '0')}-${String(Math.floor(Math.random() * 28) + 1).padStart(2, '0')}`,
    address: `北京市${['朝阳区', '海淀区', '丰台区', '西城区'][Math.floor(Math.random() * 4)]}某某街道某某号`,
    remarks: `备注信息 ${elderId || 1}`,
    emergencyContact: `紧急联系人${elderId || 1}`,
    emergencyPhone: `139${Math.floor(10000000 + Math.random() * 90000000)}`,
    allergyHistory: Math.random() > 0.5 ? '青霉素过敏' : '无',
    medicalHistory: ['高血压', '糖尿病', '心脏病'][Math.floor(Math.random() * 3)],
    specialNeeds: '特殊需求说明'
  }
}

onMounted(() => {
  loadElderDetail()
})
</script>

<style scoped>
.elder-detail-container {
  padding: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.breadcrumb {
  margin-bottom: 10px;
  padding: 0 10px;
}

.elder-info-card,
.archive-card,
.nursing-card,
.medical-history-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-operation {
  display: flex;
  gap: 10px;
}

.info-value {
  font-weight: 500;
  color: #606266;
}

.nursing-content {
  padding: 10px 0;
}

.nursing-plan-description {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.nursing-plan-description h4 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.medical-record-card {
  margin-bottom: 10px;
}

.medical-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.no-medical-records {
  text-align: center;
  padding: 40px 0;
}

:deep(.el-descriptions__label) {
  background-color: #fafafa;
  font-weight: 600;
}
</style>