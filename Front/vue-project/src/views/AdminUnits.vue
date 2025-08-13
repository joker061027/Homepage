<template>
  <div class="container">
    <div class="tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="单位与用户管理" name="units">
          <div class="header">
            <h3>机构列表</h3>
            <div>
              <el-button type="primary" @click="openCreateAgency">添加机构</el-button>
            </div>
          </div>
          <el-table :data="tableData" v-loading="loading" style="width:100%">
            <el-table-column label="序号" width="80" align="center">
              <template #default="scope">{{ (pagination.page - 1) * pagination.size + scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column prop="agenciesName" label="单位名称" min-width="200"/>
            <el-table-column prop="userCount" label="用户数量" width="100" align="center"/>
            <el-table-column prop="pendingOrderCount" label="待审核订单量" width="120" align="center"/>
            <el-table-column prop="unusedTicketCount" label="未使用餐票数" width="120" align="center"/>
            <el-table-column prop="usedTicketCount" label="已使用餐票数" width="120" align="center"/>
            <el-table-column label="操作" width="120" align="center">
              <template #default="scope">
                <el-button type="primary" link @click="openManage(scope.row)">管理</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pager">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="pagination.total"
              :page-size="pagination.size"
              :current-page="pagination.page"
              @current-change="loadPage"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 管理弹窗 -->
    <el-dialog v-model="manage.visible" width="900px" title="单位管理">
      <div class="manage-grid">
        <div class="left">
          <el-form label-width="90px">
            <el-form-item label="单位名称">
              <el-input v-model="manage.editName" />
              <el-button type="primary" class="ml8" @click="updateAgencyName">修改单位名称</el-button>
            </el-form-item>
          </el-form>

          <h4>用户：</h4>
          <el-table :data="users" size="small" style="width:100%">
            <el-table-column type="index" width="60" label="序号" />
            <el-table-column prop="fullName" label="用户" />
            <el-table-column label="操作" width="160" align="center">
              <template #default="scope">
                <el-button type="primary" size="small" text @click="editUser(scope.row)">修改</el-button>
                <el-button type="danger" size="small" text @click="deleteUser(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="mt16">
            <el-popconfirm title="删除后不可恢复，确认删除此单位？" @confirm="deleteAgency">
              <template #reference>
                <el-button type="danger">删除此单位</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <div class="right">
          <h4>添加用户</h4>
          <el-form :model="addUser" label-width="90px" class="add-user-form">
            <el-form-item label="单位">{{ manage.agency && manage.agency.agenciesName }}</el-form-item>
            <el-form-item label="用户名">
              <el-input v-model="addUser.username" />
            </el-form-item>
            <el-form-item label="联系人">
              <el-input v-model="addUser.phoneNumber" />
            </el-form-item>
            <el-form-item label="姓 名">
              <el-input v-model="addUser.fullName" />
            </el-form-item>
            <el-form-item label="密 码">
              <el-input v-model="addUser.password" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="addUser.confirm" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="createUser">确定</el-button>
              <el-button @click="resetAddUser">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-dialog>

    <!-- 新建机构弹窗（简单版） -->
    <el-dialog v-model="create.visible" title="添加机构" width="420px">
      <el-form :model="create.form" label-width="90px">
        <el-form-item label="机构ID">
          <el-input v-model="create.form.agenciesId" />
        </el-form-item>
        <el-form-item label="机构名称">
          <el-input v-model="create.form.agenciesName" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitCreateAgency">确定</el-button>
          <el-button @click="create.visible=false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { agenciesAPI, userAPI, authAPI } from '@/utils/api.js'

const activeTab = ref('units')
const loading = ref(false)
const tableData = ref([])
const pagination = ref({ page: 1, size: 10, total: 0 })

const manage = ref({ visible: false, agency: null, editName: '' })
const users = ref([])

const addUser = ref({ username: '', phoneNumber: '', fullName: '', password: '', confirm: '' })

const create = ref({ visible: false, form: { agenciesId: '', agenciesName: '' } })

const loadPage = async (page = 1) => {
  loading.value = true
  try {
    pagination.value.page = page
    const res = await agenciesAPI.getStatsPage(page - 1, pagination.value.size)
    if (res.success && res.data) {
      tableData.value = res.data.content || []
      pagination.value.total = res.data.totalElements || 0
    } else if (res && res.content) {
      tableData.value = res.content
      pagination.value.total = res.totalElements || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('加载机构列表失败')
  } finally {
    loading.value = false
  }
}

const openManage = async (row) => {
  manage.value.agency = row
  manage.value.editName = row.agenciesName
  manage.value.visible = true
  await loadUsers(row.agenciesId)
}

const loadUsers = async (agenciesId) => {
  const res = await userAPI.getAll()
  const list = Array.isArray(res) ? res : res.data || []
  users.value = list.filter(u => u.agenciesId === agenciesId)
}

const updateAgencyName = async () => {
  try {
    const agency = {
      agenciesId: manage.value.agency.agenciesId,
      agenciesName: manage.value.editName,
      status: 1,
      fkCreator: 'system',
      createDatetime: '',
      createIP: ''
    }
    const res = await agenciesAPI.create(agency)
    if ((res && res.success) || res?.agenciesId || res?.agenciesName) {
      ElMessage.success('修改成功')
      manage.value.agency.agenciesName = agency.agenciesName
      await loadPage(pagination.value.page)
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (e) {
    ElMessage.error('修改失败')
  }
}

const deleteAgency = async () => {
  try {
    await agenciesAPI.delete(manage.value.agency.agenciesId)
    ElMessage.success('删除成功')
    manage.value.visible = false
    await loadPage(1)
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const createUser = async () => {
  if (!addUser.value.password || addUser.value.password !== addUser.value.confirm) {
    ElMessage.warning('两次密码不一致')
    return
  }
  try {
    const payload = {
      username: addUser.value.username,
      fullName: addUser.value.fullName,
      password: addUser.value.password,
      phoneNumber: addUser.value.phoneNumber,
      agenciesId: manage.value.agency.agenciesId
    }
    const res = await authAPI.register(payload)
    if (res.success) {
      ElMessage.success('创建成功')
      resetAddUser()
      await loadUsers(manage.value.agency.agenciesId)
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

const resetAddUser = () => {
  addUser.value = { username: '', phoneNumber: '', fullName: '', password: '', confirm: '' }
}

const editUser = async (user) => {
  const name = await ElMessageBox.prompt('修改用户姓名', '修改', { inputValue: user.fullName }).catch(() => null)
  if (!name) return
  const payload = { ...user, fullName: name.value }
  const res = await userAPI.create(payload)
  if (res && (res.success || res.userId)) {
    ElMessage.success('已保存')
    await loadUsers(manage.value.agency.agenciesId)
  } else {
    ElMessage.error(res?.message || '保存失败')
  }
}

const deleteUser = async (user) => {
  await ElMessageBox.confirm('确认删除该用户？', '提示').catch(() => null)
  try {
    await userAPI.delete(user.userId)
    ElMessage.success('已删除')
    await loadUsers(manage.value.agency.agenciesId)
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const openCreateAgency = () => {
  create.value.visible = true
}

const submitCreateAgency = async () => {
  try {
    const payload = {
      agenciesId: (create.value.form.agenciesId || '').trim(),
      agenciesName: (create.value.form.agenciesName || '').trim(),
      status: 1,
      fkCreator: 'system',
      createDatetime: '',
      createIP: ''
    }
    const res = await agenciesAPI.create(payload)
    if (res.success || res.agenciesId) {
      ElMessage.success('创建成功')
      create.value.visible = false
      create.value.form = { agenciesId: '', agenciesName: '' }
      await loadPage(1)
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  loadPage(1)
})
</script>

<style scoped>
.container { padding: 20px; }
.header { display:flex; justify-content: space-between; align-items:center; margin-bottom: 10px; }
.pager { margin-top: 12px; text-align: right; }
.manage-grid { display: grid; grid-template-columns: 1.2fr 1fr; gap: 16px; }
.left, .right { background: #fff; padding: 8px; border-radius: 6px; }
.ml8 { margin-left: 8px; }
.mt16 { margin-top: 16px; }
.add-user-form { max-width: 360px; }
</style>


// Order branch specific code - added for merge request detection
