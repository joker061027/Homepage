import axios from "axios";
import { ElMessage } from "element-plus";

const request =axios.create({
    baseURL:'http://localhost:8081/api',
    timeout:30000
})


request.interceptors.request.use(config=>{
    config.headers['Content-Type']='application/json;charset=utf-8';
    return config
}, errror=>{
    return Promise.reject(error)
});

request.interceptors.response.use(
    response=>{
        let res =response.data;
        if (typeof res==='string'){
            res=res?JSON.parse(res):res
        }
        return res;
    },
    errror=>{
        if(errror.response.status===404){
            ElMessage.error('未找到请求接口')
        }
        else if(errror.response.status===5000){
            ElMessage.error('系统异常，请查看后端控制台报错')
        }
        else{
            console.error(error.message)
        }
        return Promise.reject(error)
    }
)
export default request