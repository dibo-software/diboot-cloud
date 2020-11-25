<template>
  <div>
    <a-tabs v-if="moduleList.length > 0">
      <template v-for="(m, i) in moduleList">
        <a-tab-pane :key="i" :tab="m">
          <list :app-module="m"></list>
        </a-tab-pane>
      </template>
    </a-tabs>
  </div>
</template>
<script>
import { dibootApi } from '@/utils/request'
import list from './list'

export default {
  name: 'IamOperationLogIndex',
  data () {
    return {
      moduleList: []
    }
  },
  methods: {
    async initModuleList () {
      const res = await dibootApi.get('/auth-server/common/moduleList')
      if (res.code === 0) {
        this.moduleList = res.data
      }
    }
  },
  components: {
    list
  },
  created () {
    this.initModuleList()
  }

}
</script>
