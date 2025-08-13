<template>
  <div>
    <!-- 第一行：未处理与已处理订单 -->
    <div class="bzdzs">
      <div style="display: flex; gap: 20px;">
        <!-- 未处理餐票订单 -->
        <div style="width: 850px;height: 400px;">
          <el-card style="max-width: 1500px;max-height: 1000px" shadow="never">
            <div style="margin-bottom: 5px;margin-top: -5px;color: #0000a6">未处理餐票订单</div>
            <el-table 
              :data="data.unsolveorder" 
              height="250" 
              stripe 
              style="width: 100%" 
              size="small" 
              class="table-unhandled"
            >
              <el-table-column 
                prop="sequence" 
                label="序号" 
                width="100" 
                header-align="center" 
                align="center" 
              />
              <el-table-column 
                prop="agenciesName" 
                label="申请单位" 
                width="250" 
                header-align="center" 
                align="center" 
              />
              <el-table-column 
                prop="orderAmount" 
                label="餐票数量" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="createDatetime" 
                label="申请日期" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                label="" 
                header-align="center" 
                align="center" 
                width="100"
              >
                <template #default="scope">
                  <el-button type="warning" text @click="check(scope.row)">
                    审核
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              background
              layout="prev, pager, next"
              :total="unsolveorderPagination.total"
              :page-size="unsolveorderPagination.pageSize"
              :current-page="unsolveorderPagination.currentPage"
              @current-change="handleUnsolveorderPageChange"
              style="float: right; margin-top: 15px;"
            />
          </el-card>
        </div>

        <!-- 已处理餐票订单 -->
        <div style="width:400px">
          <el-card style="max-width: 1500px;max-height: 1000px" shadow="never">
            <div style="margin-bottom: 5px;margin-top: -5px;color: #0000a6">已处理餐票订单</div>
            <el-table 
              :data="data.solveorder" 
              height="250" 
              stripe 
              style="width: 100%"  
              size="small" 
              class="table-handled table-common"
            >
              <el-table-column 
                prop="agenciesName" 
                label="申请单位"  
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="orderAmount" 
                label="申请数量"  
                header-align="center" 
                align="center"
              />
            </el-table>
            <el-pagination
              background
              layout="prev, pager, next"
              :total="solveorderPagination.total"
              :page-size="solveorderPagination.pageSize"
              :current-page="solveorderPagination.currentPage"
              @current-change="handleSolveorderPageChange" 
              style="float: right; margin-top: 15px;"
            />
          </el-card>
        </div>
      </div>
    </div>

    <!-- 第二行：档口用票情况与各单位剩余餐票 -->
    <div class="bzdzs2">
      <div style="display: flex; gap: 20px;">
        <!-- 档口用票情况 -->
        <div style="width: 850px;height: 400px;">
          <el-card style="max-width: 1500px;max-height: 1000px" shadow="never">
            <div style="margin-bottom: 5px;margin-top: -5px;color: #0000a6">档口用票情况</div>
            <el-table 
              :data="data.ypqk" 
              height="250" 
              stripe 
              style="width: 100%" 
              size="small"
            >
              <el-table-column 
                prop="cid" 
                label="序号" 
                width="100" 
                header-align="center" 
                align="center" 
              />
              <el-table-column 
                prop="canteenName" 
                label="食堂" 
                header-align="center" 
                align="center" 
              />
              <el-table-column 
                prop="stallId" 
                label="档口" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="stallName" 
                label="档口名称" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="unVerifiedCount" 
                label="未核销餐票" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="verifiedCount" 
                label="已核销餐票" 
                header-align="center" 
                align="center"
              />
              <el-table-column 
                label="" 
                header-align="center" 
                align="center" 
                width="100"
              >
                <el-button type="warning" text>
                  核销
                </el-button>
              </el-table-column>
            </el-table>
            <el-pagination
              background
              :page-size="ypqkPagination.pageSize"
              :pager-count="3"
              layout="prev, pager, next"
              :total="ypqkPagination.total"
              :current-page="ypqkPagination.currentPage"
              @current-change="handleYpqkPageChange"  
              style="float: right; margin-top: 15px;"
            />
          </el-card>
        </div>

        <!-- 各单位剩余餐票 -->
        <div style="width:400px">
          <el-card style="max-width: 1500px;max-height: 1000px" shadow="never">
            <div style="margin-bottom: 5px;margin-top: -5px;color: #0000a6">各单位剩余餐票</div>
            <el-table 
              :data="data.remainnum" 
              height="250" 
              stripe 
              style="width: 100%"  
              size="small" 
              class="table-handled table-common"
            >
              <el-table-column 
                prop="agenciesName" 
                label="单位"  
                header-align="center" 
                align="center"
              />
              <el-table-column 
                prop="unVerifiedCount" 
                label="餐票剩余"  
                header-align="center" 
                align="center"
              />
            </el-table>
            <el-pagination
              background
              :page-size="remainnumPagination.pageSize"
              :pager-count="5"
              layout="prev, pager, next"
              :total="remainnumPagination.total"
              :current-page="remainnumPagination.currentPage"
              @current-change="handleRemainnumPageChange"
              style="float: right; margin-top: 15px;"
            />
          </el-card>
        </div>
      </div>
    </div>

    <!-- 审核订单对话框 -->
    <el-dialog 
      title="审核订单" 
      v-model="data.formVisible" 
      width="600" 
      custom-class="custom-dialog-title"
    >
      <el-form 
        :model="data.form" 
        label-width="100px" 
        style="padding-right: 40px; padding-top: 20px"
      >
        <el-form-item label="订单编号">
          <el-input v-model="data.form.orderNumber" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item label="申请单位">
          <el-input :value="getDepartmentName(data.form.agenciesId)" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item label="申请人">
          <el-input :value="getUserName(data.form.userId)" disabled />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-input v-model="data.form.createDatetime" disabled style="width: 100%"/>
        </el-form-item>
        <div style="margin-left: 35px">
          <el-table
            :data="data.form.fullInfo"
            stripe
            style="width: 100%; margin-top: 10px;"
          >
            <el-table-column 
              type="index" 
              index-method="(index) => index + 1" 
              label="序号" 
              header-align="center" 
              align="center" 
              width="80"
            />
            <el-table-column 
              prop="typeName" 
              label="餐票类型" 
              header-align="center" 
              align="center"
            >
              <template #default="{row, $index}">
                <div style="display: inline-flex; align-items: center; justify-content: center; gap: 4px;">
                  <span>{{ row.typeName }}</span>
                  <el-tooltip effect="dark" content="点击查看详情" placement="top">
                    <svg
                      t="1754380565148"
                      class="icon clickable-icon"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="5348"
                      xmlns:xlink="http://www.w3.org/1999/xlink"
                      width="16"
                      height="16"
                      style="vertical-align: middle; margin-top: -3px;"
                      @click="showTicketDetail(row, $index)"
                    >
                      <path d="M463.99957 784.352211c0 26.509985 21.490445 48.00043 48.00043 48.00043s48.00043-21.490445 48.00043-48.00043c0-26.509985-21.490445-48.00043-48.00043-48.00043S463.99957 757.842226 463.99957 784.352211z" fill="#575B66" p-id="5349"></path>
                      <path d="M512 960c-247.039484 0-448-200.960516-448-448S264.960516 64 512 64 960 264.960516 960 512 759.039484 960 512 960zM512 128.287273c-211.584464 0-383.712727 172.128262-383.712727 383.712727 0 211.551781 172.128262 383.712727 383.712727 383.712727 211.551781 0 383.712727-172.159226 383.712727-383.712727C895.712727 300.415536 723.551781 128.287273 512 128.287273z" fill="#575B66" p-id="5350"></path>
                      <path d="M512 673.695256c-17.664722 0-32.00086-14.336138-32.00086-31.99914l0-54.112297c0-52.352533 39.999785-92.352318 75.32751-127.647359 25.887273-25.919957 52.67249-52.67249 52.67249-74.016718 0-53.343368-43.07206-96.735385-95.99914-96.735385-53.823303 0-95.99914 41.535923-95.99914 94.559333 0 17.664722-14.336138 31.99914-32.00086 31.99914s-32.00086-14.336138-32.00086-31.99914c0-87.423948 71.775299-158.559333 160.00086-158.559333s160.00086 72.095256 160.00086 160.735385c0 47.904099-36.32028 84.191695-71.424378 119.295794-27.839699 27.776052-56.575622 56.511974-56.575622 82.3356l0 54.112297C544.00086 659.328155 529.664722 673.695256 512 673.695256z" fill="#575B66" p-id="5351"></path>
                    </svg>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
            <el-table-column 
              prop="orderAmount" 
              label="订购数量" 
              header-align="center" 
              align="center" 
              width="120"
            />
          </el-table>
        </div>
        <div style="margin-left: 20px;margin-top: 20px">
          <div class="date-picker-container">
            <div class="date-picker-label">重设有效期</div>
            <div class="date-picker-wrapper">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY年M月D日"
                @change="handleDateChange"
                style="width: 100%"
              />
            </div>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button 
            type="primary" 
            @click="passcheck" 
            round 
            size="large" 
            style="background-color: #289e28; border-color: #289e28; color: white; width: 120px;"
          >
            通过申请
          </el-button>
          <el-button 
            @click="data.formVisible = false" 
            round 
            size="large" 
            style="background-color: #dc3545; border-color: #dc3545; color: white; width: 120px; margin-left: 10px;"
          >
            驳回申请
          </el-button>
          <el-button 
            @click="data.formVisible = false" 
            round 
            size="large" 
            style="background-color: #7f8c8d; border-color: #7f8c8d; color: white; width: 120px; margin-left: 10px;"
          >
            取消
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 餐票类型详情对话框 -->
    <el-dialog 
      v-model="data.formVisible2" 
      width="400"  
      custom-class="custom-dialog-title"
    >
      <el-form 
        title="餐票类型" 
        :model="data.form" 
        label-width="100px" 
        style="padding-right: 40px; padding-top: 20px"
      >
        <el-form-item style="text-align: center; margin-bottom: 30px;margin-top:-20px;margin-left:80px;color: #0000a6 ">
          <span style="font-size: 30px; font-weight: normal;">餐票类型</span>
        </el-form-item>
        <el-form-item label="类型名称">
          <el-input v-model="data.form.typeName" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item label="可用食堂">
          <el-input v-model="data.form.availableCanteens" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item label="面额">
          <el-input v-model="data.form.unitPrice" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item label="有效期天数">
          <el-input :value="data.form.validityDays" disabled style="width: 100%"/>
        </el-form-item>
        <el-form-item>
          <span class="form-text">从当前起至{{ dateRange && dateRange[1] ? dateRange[1] : '未设置' }}</span>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 餐票生成对话框 -->
    <el-dialog  
      v-model="data.formVisible3" 
      width="600" 
      custom-class="custom-dialog-title"
    >
      <el-form-item style="text-align: center; margin-bottom: 10px;margin-top:10px;margin-left:225px;color: #0000a6 ">
        <span style="font-size: 25px; font-weight: normal;">餐票生成</span>
      </el-form-item>
      <el-form 
        :model="data.form" 
        label-width="100px" 
        style="padding-right: 40px; padding-top: 20px"
      >
        <el-form-item label="订单编号">
          <span class="form-text">{{data.form.orderNumber}}</span>
        </el-form-item>
        <el-form-item label="申请单位">
          <span class="form-text">{{getDepartmentName(data.form.agenciesId)}}</span>
        </el-form-item>
        <el-form-item label="申请人">
          <span class="form-text">{{getUserName(data.form.userId)}}</span>
        </el-form-item>
        <el-form-item label="申请时间">
          <span class="form-text">{{data.form.createDatetime}}</span>
        </el-form-item>
        <el-form-item label="重设有效期">
          <span class="form-text">
            {{dateRange && dateRange.length === 2 ? `${dateRange[0]} 至 ${dateRange[1]}` : '未设置有效期'}}
          </span>
        </el-form-item>
        <el-form-item label="申请餐票"></el-form-item>
        <div style="margin-left: 35px">
          <el-table
            :data="data.form.fullInfo"
            stripe
            style="width: 100%; margin-top: 10px;"
          >
            <el-table-column 
              type="index" 
              index-method="(index) => index + 1" 
              label="序号" 
              header-align="center" 
              align="center" 
              width="80"
            />
            <el-table-column 
              prop="typeName" 
              label="餐票类型" 
              header-align="center" 
              align="center"
            >
              <template #default="{row, $index}">
                <div style="display: inline-flex; align-items: center; justify-content: center; gap: 4px;">
                  <span>{{ row.typeName }}</span>
                  <el-tooltip effect="dark" content="点击查看详情" placement="top">
                    <svg
                      t="1754380565148"
                      class="icon clickable-icon"
                      viewBox="0 0 1024 1024"
                      version="1.1"
                      xmlns="http://www.w3.org/2000/svg"
                      p-id="5348"
                      xmlns:xlink="http://www.w3.org/1999/xlink"
                      width="16"
                      height="16"
                      style="vertical-align: middle; margin-top: -3px;"
                      @click="showTicketDetail(row, $index)"
                    >
                      <path d="M463.99957 784.352211c0 26.509985 21.490445 48.00043 48.00043 48.00043s48.00043-21.490445 48.00043-48.00043c0-26.509985-21.490445-48.00043-48.00043-48.00043S463.99957 757.842226 463.99957 784.352211z" fill="#575B66" p-id="5349"></path>
                      <path d="M512 960c-247.039484 0-448-200.960516-448-448S264.960516 64 512 64 960 264.960516 960 512 759.039484 960 512 960zM512 128.287273c-211.584464 0-383.712727 172.128262-383.712727 383.712727 0 211.551781 172.128262 383.712727 383.712727 383.712727 211.551781 0 383.712727-172.159226 383.712727-383.712727C895.712727 300.415536 723.551781 128.287273 512 128.287273z" fill="#575B66" p-id="5350"></path>
                      <path d="M512 673.695256c-17.664722 0-32.00086-14.336138-32.00086-31.99914l0-54.112297c0-52.352533 39.999785-92.352318 75.32751-127.647359 25.887273-25.919957 52.67249-52.67249 52.67249-74.016718 0-53.343368-43.07206-96.735385-95.99914-96.735385-53.823303 0-95.99914 41.535923-95.99914 94.559333 0 17.664722-14.336138 31.99914-32.00086 31.99914s-32.00086-14.336138-32.00086-31.99914c0-87.423948 71.775299-158.559333 160.00086-158.559333s160.00086 72.095256 160.00086 160.735385c0 47.904099-36.32028 84.191695-71.424378 119.295794-27.839699 27.776052-56.575622 56.511974-56.575622 82.3356l0 54.112297C544.00086 659.328155 529.664722 673.695256 512 673.695256z" fill="#575B66" p-id="5351"></path>
                    </svg>
                  </el-tooltip>
                </div>
              </template>
            </el-table-column>
            <el-table-column 
              prop="orderAmount" 
              label="订购数量" 
              header-align="center" 
              align="center" 
              width="120"
            />
          </el-table>
        </div>
      </el-form>
      <template #footer>
        <div style="margin-left: 20px ;margin-right: 20px;margin-top:10px;">
          <el-progress
            :percentage="progressPercentage"
            :color="Color"
            :stroke-width="40"
            :format="format"
            :text-inside="true"
          />
        </div>
      </template>
    </el-dialog>

    <!-- 餐票生成完成对话框 -->
    <el-dialog 
      v-model="data.formVisible4" 
      width="1200" 
      custom-class="custom-dialog-title"
    >
      <el-form-item style="text-align: center; margin-bottom: 10px; margin-top: 10px; color: #0000a6; margin-left: 490px">
        <span style="font-size: 25px; font-weight: normal;">餐票生成完成</span>
      </el-form-item>

      <!-- 基本信息行 -->
      <el-row :gutter="20" class="compact-form-row">
        <el-col :span="8">
          <el-form-item label="订单编号:" class="compact-form-item">
            <span class="form-text">{{ data.form.orderNumber }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申请单位:" class="compact-form-item">
            <span class="form-text">{{ getDepartmentName(data.form.agenciesId) }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="申请人:" class="compact-form-item">
            <span class="form-text">{{ getUserName(data.form.userId) }}</span>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 餐票信息表格 -->
      <div style="margin: 20px 0;">
        <el-table
          :data="formatTicketData(data.form.fullInfo)"
          border
          style="width: 100%"
          size="small"
        >
          <el-table-column
            v-for="(col, index) in ticketTableColumns"
            :key="index"
            :prop="col.prop"
            :label="col.label"
            header-align="center"
            align="center"
            :width="col.width"
          />
        </el-table>
      </div>

      <template #footer>
        <div style="text-align: center;">
          <el-button 
            type="primary" 
            @click="data.formVisible4 = false" 
            round 
            size="large"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, ref } from "vue";
import request from "@/utils/request";
import { ElMessage } from 'element-plus';

// 加载状态
const loading = ref(false);

// 核心数据存储
const data = reactive({
  // 表格数据
  unsolveorder: [],  // 分页后的未处理订单
  allUnsolveOrders: [], // 完整未处理订单数据
  solveorder: [],
  allSolveOrders: [],     // 完整已处理订单数据
  ypqk: [],
  allYpqk: [],            // 完整档口用票数据
  remainnum: [],
  allRemainnum: [],       // 完整剩余餐票数据
  
  // 对话框控制
  formVisible: false,    // 审核订单对话框
  formVisible2: false,   // 餐票类型详情对话框
  formVisible3: false,   // 餐票生成对话框
  formVisible4: false,   // 生成完成对话框
  
  // 表单数据
  form: { 
    fullInfo: [], 
    orderNumber: '',
    agenciesId: '',
    userId: '',
    createDatetime: '',
    typeName: '',
    availableCanteens: '',
    unitPrice: '',
    validityDays: ''
  },
  
  page: 0,
  size: 5,
  total: 0
})

// 餐票表格列配置
const ticketTableColumns = [
  { prop: 'index', label: '序号', width: '80' },
  { prop: 'typeName', label: '餐票类型' },
  { prop: 'orderAmount', label: '订购数量', width: '120' }
]

// 分页配置
const unsolveorderPagination = ref({
  currentPage: 1,
  pageSize: 5,
  total: 0
});

const solveorderPagination = ref({
  currentPage: 1,
  pageSize: 5,
  total: 0
});

const ypqkPagination = ref({  // 档口数据分页配置
  currentPage: 1,
  pageSize: 5,
  total: 0
})

const remainnumPagination = ref({  // 剩余餐票分页配置
  currentPage: 1,
  pageSize: 5,
  total: 0
});

// 进度条相关
const progressPercentage = ref(0);
const progressInterval = ref(null);
const Color = [{ color: '#05a135' }];
const dateRange = ref([]);

// 格式化餐票表格数据
const formatTicketData = (tickets) => {
  if (!tickets) return []
  return tickets.map((item, index) => ({
    ...item,
    index: index + 1
  }))
}

// 进度条格式化
const format = (percentage) => (percentage === 100 ? '' : ``)

// 开始进度条动画
const startProgress = () => {
  progressPercentage.value = 0;
  clearInterval(progressInterval.value);
  progressInterval.value = setInterval(() => {
    if (progressPercentage.value >= 100) {
      clearInterval(progressInterval.value);
      data.formVisible3 = false;
      data.formVisible4 = true;
    } else {
      progressPercentage.value += 2;
    }
  }, 100);
};

// 审核通过处理
const passcheck = async () => {
  data.formVisible = false;
  data.formVisible3 = true;
  startProgress();
};

// 计算有效期天数
const calculateDays = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  const diffTime = end - start;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
};

// 显示餐票类型详情
const showTicketDetail = async (row, index) => {
  data.formVisible2 = true;
  if (dateRange.value && dateRange.value.length === 2) {
    const days = calculateDays(dateRange.value[0], dateRange.value[1]);
    data.form.validityDays = `${days}天`;
  }
  try {
    const typeId = data.form.fullInfo[index].typeId;
    const response = await request.get(`/api/ticket-type/${typeId}`); // 假设接口
    if (response.code === '200' || response.status === 200) {
      data.form = {
        ...data.form,
        typeName: response.data.typeName || '未知类型',
        availableCanteens: Array.isArray(response.data.availableCanteens)
          ? response.data.availableCanteens.join(', ')
          : '无限制',
        unitPrice: response.data.unitPrice ? `¥${response.data.unitPrice}` : '未知',
      };
    } else {
      ElMessage.error(response.msg || '获取餐票类型详情失败');
    }
  } catch (error) {
    console.error('获取餐票类型详情失败:', error);
    ElMessage.error('获取餐票类型详情失败: ' + (error.response?.data?.msg || error.message));
  }
  console.log('餐票类型',data.form)
}


// 打开审核对话框
const check = async (row) => {
  data.formVisible = true;
  data.form = { ...row };
  
  // 设置默认日期范围
  const today = new Date();
  const nextYear = new Date();
  nextYear.setFullYear(today.getFullYear() + 1);
  dateRange.value = [
    today.toISOString().split('T')[0],
    nextYear.toISOString().split('T')[0]
  ];
  
  try {
    const fullInfo = await loadOrderInfo(row.orderId);
    data.form.fullInfo = Array.isArray(fullInfo) ? fullInfo : [];
  } catch (error) {
    console.error('获取订单完整信息失败:', error);
    ElMessage.error('获取订单完整信息失败');
    data.form.fullInfo = [];
  }
}

// 加载订单详情
const loadOrderInfo = async (orderId) => {
  try {
    const response = await request.get(`/ott/order/${orderId}`);
    if (response.code === '200' || response.status === 200) {
      return response.data;
    } else {
      ElMessage.error(response.msg || '获取订单完整信息失败');
      return null;
    }
  } catch (error) {
    // console.error('加载订单详情失败:', error);
    ElMessage.error('加载订单失败: ' + (error.response?.data?.msg || error.message));
    return null;
  }
}

// 部门与用户数据加载
const departmentList = ref([]);
const userList = ref([]);

const loadDepartments = async () => {
  try {
    const response = await request.get('/agencies');
    let departmentData = response.data?.list || response.data || response;
    if (Array.isArray(departmentData)) {
      departmentList.value = departmentData;
    } else {
      ElMessage.error('部门数据格式不正确');
      departmentList.value = [];
    }
  } catch (error) {
    // console.error('加载部门失败:', error);
    ElMessage.error('加载部门失败: ' + (error.response?.data?.msg || error.message));
    departmentList.value = [];
  }
  console.log('部门信息',departmentList.value)
}

const loadUsers = async () => {
  try {
    const response = await request.get('/users');
    let userData = response.data?.list || response.data || response;
    console.log('用户',userData)
    if (Array.isArray(userData)) {
      userList.value = userData;
    } else {
      ElMessage.error('用户数据格式不正确');
      userList.value = [];
    }
  } catch (error) {
    // console.error('加载用户失败:', error);
    ElMessage.error('加载用户失败: ' + (error.response?.data?.msg || error.message));
    userList.value = [];
  }
  console.log('用户信息',userList.value)
}

// 获取部门名称
const getDepartmentName = (id) => {
  if (!id) return '未知部门';
  const department = departmentList.value.find(item => item.agenciesId === id);
  return department?.agenciesName || `未知部门(${id})`;
}

// 获取用户姓名
const getUserName = (id) => {
  if (!id) return '1未知用户';
  const user = userList.value.find(item => item.userId === id);
  return user?.fullName || user?.name || user?.userName || `32未知用户(${id})`;
}

// 数据加载方法
const loadUnsolveOrders = () => {
  loading.value = true;
  request.get('/ott/joined/by-status?status=0')
    .then((res) => {
      data.allUnsolveOrders = res.data || [];
      data.allUnsolveOrders.forEach((item, index) => {
        item.sequence = index + 1;
        if (item.createDatetime) {
          item.createDatetime = item.createDatetime.split('T')[0];
        }
      });
      unsolveorderPagination.value.total = data.allUnsolveOrders.length;
      handleUnsolveorderPageChange(1);
    })
    .catch(error => {
      console.error('加载未处理订单失败:', error);
      ElMessage.error('加载未处理订单失败，请稍后重试');
    })
    .finally(() => {
      loading.value = false;
    });
}

const loadSolveOrders = () => {
  loading.value = true;
  request.get('/ott/joined/by-status?status=1')
    .then((res) => {
      data.allSolveOrders = res.data || [];
      data.allSolveOrders.forEach(item => {
        if (item.handleDatetime) {
          item.handleDatetime = item.handleDatetime.split('T')[0];
        }
      });
      solveorderPagination.value.total = data.allSolveOrders.length;
      handleSolveorderPageChange(1);
    })
    .catch(error => {
      console.error('加载已处理订单失败:', error);
      ElMessage.error('加载已处理订单失败，请稍后重试');
    })
    .finally(() => {
      loading.value = false;
    });
};

const loadYpqk = () => {
  loading.value = true;
  request.get('/tickets/count-verified-status')
    .then((res) => {
      data.allYpqk = res.data || [];
      data.allYpqk.forEach((item, index) => {
        item.cid = index + 1;
      });
      ypqkPagination.value.total = data.allYpqk.length;
      handleYpqkPageChange(1);
    })
    .catch(error => {
      ElMessage.error('加载档口用票数据失败，请稍后重试');
    })
    .finally(() => {
      loading.value = false;
    });
};

const loadRemainnum = () => {
  loading.value = true;
  request.get('/tickets/unverified/sum-by-agency')
    .then((res) => {
      data.allRemainnum = res.data || [];
      data.allRemainnum.forEach((item, index) => {
        item.sequence = index + 1;
        console.log(res.data)
      });
      remainnumPagination.value.total = data.allRemainnum.length;
      handleRemainnumPageChange(1);
    })
    .catch(error => {
      console.error('加载各单位剩余餐票失败:', error);
      ElMessage.error('加载各单位剩余餐票失败，请稍后重试');
    })
    .finally(() => {
      loading.value = false;
    });
};

// 分页处理方法
const handleUnsolveorderPageChange = (page) => {
  unsolveorderPagination.value.currentPage = page;
  const startIndex = (page - 1) * unsolveorderPagination.value.pageSize;
  const endIndex = startIndex + unsolveorderPagination.value.pageSize;
  data.unsolveorder = data.allUnsolveOrders.slice(startIndex, endIndex);
}

const handleSolveorderPageChange = (page) => {
  solveorderPagination.value.currentPage = page;
  const startIndex = (page - 1) * solveorderPagination.value.pageSize;
  const endIndex = startIndex + solveorderPagination.value.pageSize;
  data.solveorder = data.allSolveOrders.slice(startIndex, endIndex);
};

const handleYpqkPageChange = (page) => {
  ypqkPagination.value.currentPage = page;
  const startIndex = (page - 1) * ypqkPagination.value.pageSize;
  const endIndex = startIndex + ypqkPagination.value.pageSize;
  data.ypqk = data.allYpqk.slice(startIndex, endIndex);
};

const handleRemainnumPageChange = (page) => {
  remainnumPagination.value.currentPage = page;
  const startIndex = (page - 1) * remainnumPagination.value.pageSize;
  const endIndex = startIndex + remainnumPagination.value.pageSize;
  data.remainnum = data.allRemainnum.slice(startIndex, endIndex);
};

// 设置默认日期范围
const setDefaultDateRange = () => {
  const today = new Date();
  const nextYear = new Date();
  nextYear.setDate(today.getDate() + 365);
  const formatDate = (date) => {
    return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
  };
  dateRange.value = [formatDate(today), formatDate(nextYear)];
}

// 处理日期变化
const handleDateChange = (val) => {
  if (val && val.length === 2) {
    const days = calculateDays(val[0], val[1]);
    if (data.formVisible2) {
      data.form.validityDays = `${days}天`;
    }
  }
}

// 页面挂载时初始化
onMounted(async () => {
  setDefaultDateRange();
  await Promise.all([
    loadDepartments(),
    loadUsers(),
    loadUnsolveOrders(),
    loadSolveOrders(),
    loadYpqk(),
    loadRemainnum()
  ]);
});
</script>

<style scoped>
.bzdzs {
  display: flex;
  flex-direction: column;
  margin-top: 30px;
  margin-left: auto;
  margin-right: auto;
  max-width: 1300px;
}

.bzdzs2 {
  display: flex;
  flex-direction: column;
  margin-top: -30px;
  margin-left: auto;
  margin-right: auto;
  max-width: 1300px;
}

/* 表格样式统一 */
::v-deep .table-common th,
::v-deep .table-common td {
  padding: 7.5px 8px !important;
  font-size: 12px;
  line-height: 10px;
  text-align: left;
}

/* 表单样式调整 */
:deep(.el-form-item__label) {
  padding-right: 20px !important;
}

:deep(.el-form-item) {
  margin-bottom: 10px;
}

/* 表格行间距 */
:deep(.el-table--small .el-table__cell) {
  padding: 4px 0;
}

:deep(.el-table--small .el-table__header .el-table__cell) {
  padding: 8px 0;
}

:deep(.el-table--small .el-table__body .el-table__row) {
  height: 36px;
}

/* 日期选择器样式 */
.date-picker-container {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
}

.date-picker-label {
  padding: 0 15px;
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
  width: 120px;
}

.date-picker-wrapper {
  flex: 1;
}

:deep(.el-form-item__content) {
  width: 100%;
  padding-left: 20px;
}

:deep(.el-table) {
  width: 100% !important;
}

:deep(.el-date-editor) {
  border-radius: 4px;
  width: 100% !important;
}

/* 对话框样式 */
:deep(.el-dialog.custom-dialog-title .el-dialog__title) {
  color: blue !important;
  text-align: center !important;
  width: 100% !important;
  font-weight: bold !important;
}

:deep(.el-dialog) {
  min-width: 500px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 0;
}

:deep(.el-progress-bar__outer) {
  background-color: #4e4e4e !important;
}

/* 紧凑表单样式 */
:deep(.el-dialog.custom-dialog-title .compact-form-row) {
  margin-bottom: 10px;
}

:deep(.el-dialog.custom-dialog-title .compact-form-item) {
  margin-bottom: 0 !important;
  display: flex !important;
  align-items: center !important;
}

:deep(.el-dialog.custom-dialog-title .compact-form-item .el-form-item__label) {
  padding-right: 5px !important;
  width: auto !important;
  flex-shrink: 0 !important;
  line-height: 1.2 !important;
}

:deep(.el-dialog.custom-dialog-title .compact-form-item .el-form-item__content) {
  flex: 1 !important;
  padding-left: 0 !important;
  line-height: 1.2 !important;
}

:deep(.el-dialog.custom-dialog-title .form-text) {
  display: inline-block;
  vertical-align: middle;
}
</style>