<template>
  <el-dialog title="添加餐票类型" v-model="dialogVisible" :close-on-click-modal="false" 
  width="500px" @close="handleClose">

    <el-form ref="ticketForm" :model="formData" :rules="rules" label-width="100px">
      <!-- 原有表单项保持不变 -->
      <el-form-item label="餐票名称" prop="typeName">
        <el-input v-model="formData.typeName" placeholder="请输入餐票名称" />
      </el-form-item>

      <el-form-item label="可用食堂" prop="canteenIds">
        <el-select v-model="formData.canteenIds" multiple placeholder="请选择可用食堂" class="canteen-select">
          <el-option
            v-for="canteen in canteens"
            :key="canteen.canteenId"
            :label="canteen.canteenName"
            :value="canteen.canteenId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="面额" prop="value">
        <el-input v-model.number="formData.value" placeholder="请输入金额" />
      </el-form-item>
      <!-- 新增：默认有效期（日期选择器） -->
      <el-form-item label="默认有效期" prop="overtime">
        <el-date-picker
          v-model="formData.overtime"
          type="date"
          placeholder="请选择有效期截止日期"
          value-format="YYYY-MM-DD"  
          :disabled-date="disabledPastDate"  
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getCanteens, createTicketType } from '../services/api3'; 

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['close', 'refreshList']);

const dialogVisible = ref(props.visible);
const canteens = ref([]);
// 表单数据：overtime为日期字符串（YYYY-MM-DD）
const formData = ref({
  typeName: '',
  canteenIds: [],
  value: null,
  status: 1,
  overtime: ''  // 存储日期字符串
});

// 验证规则：调整为日期验证
const rules = ref({
  typeName: [
    { required: true, message: '请输入餐票名称', trigger: 'blur' },
    { max: 10, message: '名称长度不能超过10字符', trigger: 'blur' }
  ],
  canteenIds: [
    { required: true, message: '请选择可用食堂', trigger: 'change' },
    { type: 'array', min: 1, message: '至少选择一个食堂', trigger: 'change' }
  ],
  value: [
    { required: true, message: '请输入面额', trigger: 'blur' },
    { type: 'number', message: '面额必须为数字', trigger: 'blur' }
  ],
  // 有效期验证：必填
  overtime: [
    { required: true, message: '请选择默认有效期', trigger: 'change' }
  ]
});

// 禁止选择过去的日期
const disabledPastDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7; // 不包含今天之前的日期
};

const ticketForm = ref(null);

watch(() => props.visible, (val) => {
  dialogVisible.value = val;
});

onMounted(() => {
  fetchCanteens();
});

const fetchCanteens = async () => {
  try {
    const canteenData = await getCanteens();
    canteens.value = canteenData;
  } catch (error) {
    console.error('获取食堂列表异常：', error);
    ElMessage.error(error.message || '网络错误，无法获取食堂数据');
  }
};

const handleClose = () => {
  emit('close');
  resetForm();
};

const handleCancel = () => {
  handleClose();
};

const handleConfirm = async () => {
  try {
    await ticketForm.value.validate();
    const submitData = { ...formData.value };
    await createTicketType(submitData);  // 后端需接收日期字符串（YYYY-MM-DD）
    ElMessage.success('餐票类型添加成功');
    emit('close');
    emit('refreshList');
    resetForm();
  } catch (error) {
    console.error('添加失败：', error);
    if (error.name !== 'ValidationError') {
      ElMessage.error(error.message || '添加失败，请重试');
    }
  }
};

const resetForm = () => {
  ticketForm.value?.resetFields();
  formData.value.status = 1;
  formData.value.canteenIds = [];
  formData.value.overtime = '';  // 重置日期
};
</script>

<style scoped>
.canteen-select {
  width: 100%;
}
</style>