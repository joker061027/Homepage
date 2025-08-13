<template>
  <div class="login-container">//增加差异，和代码无关
    <div class="auth-card">
      <div class="auth-header">
        <h2>数字餐票系统</h2>
        <p>{{ isLogin ? '请登录您的账户' : '请注册新账户' }}</p>
      </div>
      
      <!-- 登录表单 -->
      <el-form
        v-if="isLogin"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="auth-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <User />
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <Lock />
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <div class="auth-options">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="auth-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-else
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="auth-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <User />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="fullName">
          <el-input
            v-model="registerForm.fullName"
            placeholder="请输入真实姓名"
            size="large"
            clearable
          >
            <template #prefix>
              <User />
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
          >
            <template #prefix>
              <Lock />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleRegister"
          >
            <template #prefix>
              <Lock />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phoneNumber">
          <el-input
            v-model="registerForm.phoneNumber"
            placeholder="请输入手机号码"
            size="large"
            clearable
          >
            <template #prefix>
              <Phone />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="agenciesId">
          <el-select
            v-model="registerForm.agenciesId"
            placeholder="请选择所属部门"
            size="large"
            style="width: 100%"
          >
            <el-option
              v-for="agency in agenciesList"
              :key="agency.agenciesId"
              :label="agency.agenciesName"
              :value="agency.agenciesId"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="auth-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="auth-footer">
        <p v-if="isLogin">
          还没有账户？<el-link type="primary" :underline="false" @click="switchToRegister">立即注册</el-link>
        </p>
        <p v-else>
          已有账户？<el-link type="primary" :underline="false" @click="switchToLogin">立即登录</el-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import { authAPI, agenciesAPI } from '@/utils/api.js'

const router = useRouter()
const loginFormRef = ref()
const registerFormRef = ref()
const loading = ref(false)
const isLogin = ref(true)

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 注册表单数据
const registerForm = reactive({
  username: '',
  fullName: '',
  password: '',
  confirmPassword: '',
  phoneNumber: '',
  agenciesId: ''
})

// 部门列表
const agenciesList = ref([])

// 登录表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 注册表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  phoneNumber: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ],
  agenciesId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用后端登录API
    const result = await authAPI.login({
      username: loginForm.username,
      password: loginForm.password,
      rememberMe: loginForm.rememberMe
    })

    if (result.success) {
      ElMessage.success('登录成功！')

      // 保存登录信息到localStorage
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('token', result.data.token)
      localStorage.setItem('tokenType', result.data.tokenType)
      localStorage.setItem('userId', result.data.userId)
      localStorage.setItem('username', result.data.username)
      localStorage.setItem('fullName', result.data.fullName)
      localStorage.setItem('roleId', result.data.roleId)
      localStorage.setItem('roleName', result.data.roleName)
      localStorage.setItem('agenciesId', result.data.agenciesId)
      localStorage.setItem('agenciesName', result.data.agenciesName)

      if (loginForm.rememberMe) {
        localStorage.setItem('rememberMe', 'true')
        localStorage.setItem('savedUsername', loginForm.username)
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('savedUsername')
      }

      // 跳转到首页
      router.push('/ticket-management')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('网络错误，请检查服务器连接！')
  } finally {
    loading.value = false
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    loading.value = true

    // 调用后端注册API
    const result = await authAPI.register({
      username: registerForm.username,
      fullName: registerForm.fullName,
      password: registerForm.password,
      phoneNumber: registerForm.phoneNumber,
      agenciesId: registerForm.agenciesId
    })

    if (result.success) {
      ElMessage.success('注册成功！请登录')
      
      // 清空注册表单
      Object.keys(registerForm).forEach(key => {
        registerForm[key] = ''
      })
      
      // 切换到登录页面
      isLogin.value = true
    } else {
      ElMessage.error(result.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error('网络错误，请检查服务器连接！')
  } finally {
    loading.value = false
  }
}

// 切换到注册页面
const switchToRegister = () => {
  isLogin.value = false
  // 清空登录表单
  Object.keys(loginForm).forEach(key => {
    if (key !== 'rememberMe') {
      loginForm[key] = ''
    }
  })
}

// 切换到登录页面
const switchToLogin = () => {
  isLogin.value = true
  // 清空注册表单
  Object.keys(registerForm).forEach(key => {
    registerForm[key] = ''
  })
}

// 加载部门列表
const loadAgenciesList = async () => {
  try {
    const result = await agenciesAPI.getAll()
    if (Array.isArray(result)) {
      agenciesList.value = result
    } else if (result && result.success) {
      agenciesList.value = result.data || []
    } else {
      agenciesList.value = []
    }
  } catch (error) {
    console.error('加载部门列表失败:', error)
    // 使用默认部门列表作为后备
    agenciesList.value = [
      { agenciesId: 'A001', agenciesName: '信息技术部' },
      { agenciesId: 'A002', agenciesName: '人力资源部' },
      { agenciesId: 'A003', agenciesName: '财务部' }
    ]
  }
}

// 页面加载时检查是否有记住的用户名
const checkRememberedUser = () => {
  const rememberMe = localStorage.getItem('rememberMe')
  const savedUsername = localStorage.getItem('savedUsername')
  
  if (rememberMe === 'true' && savedUsername) {
    loginForm.username = savedUsername
    loginForm.rememberMe = true
  }
}

// 初始化
onMounted(() => {
  checkRememberedUser()
  loadAgenciesList()
})
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  margin: 0;
  position: fixed;
  top: 0;
  left: 0;
}

.auth-card {
  width: 100%;
  max-width: 500px;
  min-width: 450px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  padding: 50px;
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-header h2 {
  color: #333;
  font-size: 32px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.auth-header p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.auth-form {
  margin-bottom: 30px;
}

.auth-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.auth-button {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: 500;
}

.auth-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.auth-footer p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .auth-card {
    min-width: auto;
    max-width: 90%;
    padding: 40px 30px;
  }

  .auth-header h2 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .auth-card {
    padding: 30px 20px;
  }

  .auth-header h2 {
    font-size: 24px;
  }

  .auth-header p {
    font-size: 14px;
  }
}
</style>
// Order branch specific code - added for merge request detection
