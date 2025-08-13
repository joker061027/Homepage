<template>
  <el-dialog title="修改餐票类型" v-model="dialogVisible" :close-on-click-modal="false"
    width="500px" @close="handleClose">
    <el-form ref="editForm" :model="formData" :rules="rules" label-width="100px">
      <!-- 原有表单项保持不变 -->
      <el-form-item label="餐票名称" prop="typeName">
        <el-input v-model="formData.typeName" placeholder="请输入餐票名称" />
      </el-form-item>

      <el-form-item label="可用食堂" prop="canteenIds">
        <el-select v-model="formData.canteenIds" placeholder="请选择食堂" multiple checkbox>
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

      <!--默认有效期（日期选择器-->
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
import { getCanteens, updateTicket } from '../services/api3'; 

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  editData: {
    type: [Object, null],
    default: null
  }
});

const emit = defineEmits(['close', 'refreshList']);
const dialogVisible = ref(props.visible);
const canteens = ref([]);
// 表单数据：overtime为日期字符串
const formData = ref({
  typeId: '',
  typeName: '',
  canteenIds: [],
  value: null,
  overtime: ''  // 存储日期字符串（YYYY-MM-DD）
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
  overtime: [
    { required: true, message: '请选择默认有效期', trigger: 'change' }
  ]
});

// 禁止选择过去的日期
const disabledPastDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7;
};

const editForm = ref(null);

watch(() => props.visible, (val) => {
  dialogVisible.value = val;
});

// 监听编辑数据变化，同步日期值
watch(() => props.editData, (val) => {
  if (val) {
    formData.value = { 
      typeId: val.typeId,
      typeName: val.typeName,
      canteenIds: val.canteenIds || [],
      value: val.value,
      overtime: val.overtime  // 后端返回的是YYYY-MM-DD格式字符串
    };
  }
}, { immediate: true });

onMounted(() => {
  fetchCanteens();
});

const fetchCanteens = async () => {
  try {
    const data = await getCanteens();
    canteens.value = data;
  } catch (error) {
    console.error('获取食堂列表失败：', error);
    ElMessage.error(error.message || '获取食堂数据失败');
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
    await editForm.value.validate();
    await updateTicket(formData.value);  // 后端需接收日期字符串

    ElMessage.success('更新成功');
    emit('close');
    emit('refreshList');
    resetForm();
  } catch (error) {
    console.error('更新失败：', error);
    if (error.name !== 'ValidationError') {
      ElMessage.error(error.message || '更新失败，请重试');
    }
  }
};

const resetForm = () => {
  editForm.value?.resetFields();
  formData.value.canteenIds = [];
  formData.value.overtime = '';  // 重置日期
};
</script>