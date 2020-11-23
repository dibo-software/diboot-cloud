<template>
  <div class="dashboard">
    <h2>欢迎您，{{ nickname() }}！</h2>
    <a-button @click="eventOne">测试1</a-button>
    <a-button @click="eventOne">测试2</a-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { dibootApi } from '@/utils/request'

export default {
  name: 'Dashboard',
  data () {
    return {
    }
  },
  created () {
  },
  methods: {
    ...mapGetters(['nickname']),
    async eventOne () {
      const res = await dibootApi.get('/example-api/hello')
      console.log('事件响应', res.data)
    }
  }
}
</script>

<style scoped>
  .dashboard {
    min-height: 360px;
    background: #fff;
  }
  .dashboard h2 {
    line-height: 48px;
    padding-left: 24px;
  }
</style>
