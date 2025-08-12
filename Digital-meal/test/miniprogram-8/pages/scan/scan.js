Page({
  data: {
    result: null,
    currentCode: '',
    loading: false
  },

  // 扫描二维码
  scanCode() {
    const that = this
    wx.scanCode({
      onlyFromCamera: true,
      success(res) {
        const code = res.result
        that.setData({ 
          currentCode: code,
          result: null
        })
        that.checkTicket(code)
      },
      fail(err) {
        console.error('扫描失败:', err)
        wx.showToast({
          title: '扫描失败',
          icon: 'none'
        })
      }
    })
  },

  // 验证餐票
  checkTicket(code) {
    if (this.data.loading) return
    
    this.setData({ loading: true })
    wx.showLoading({ title: '验证中...', mask: true })
    
    wx.request({
      url: 'http://localhost:8080/api/ticket/check',
      method: 'GET',
      data: { code },
      success: (res) => {
        console.log('验证结果:', res.data)
        if (res.statusCode === 200 && res.data.code === 200) {
          this.setData({ 
            result: {
              ...res.data.data,
              ticketUsed: !res.data.data.valid
            }
          })
        } else {
          wx.showToast({
            title: res.data.message || '验证失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        })
      },
      complete: () => {
        this.setData({ loading: false })
        wx.hideLoading()
      }
    })
  },

  // 使用餐票
  useTicket() {
    if (this.data.loading) return
    
    const { currentCode } = this.data
    this.setData({ loading: true })
    wx.showLoading({ title: '使用中...', mask: true })
    
    wx.request({
      url: 'http://localhost:8080/api/ticket/use',
      method: 'POST',
      data: { code: currentCode },
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          wx.showToast({
            title: '使用成功',
            icon: 'success'
          })
          this.checkTicket(currentCode)
        } else {
          wx.showToast({
            title: res.data.message || '使用失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        wx.showToast({
          title: '网络错误，请重试',
          icon: 'none'
        })
      },
      complete: () => {
        this.setData({ loading: false })
        wx.hideLoading()
      }
    })
  },

  // 格式化日期
  formatDate(timestamp) {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
  }
})