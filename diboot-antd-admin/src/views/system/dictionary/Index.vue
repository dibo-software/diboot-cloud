<template>
  <div>
    <a-tabs v-if="moduleList.length > 0">
      <template v-for="(m, i) in moduleList">
        <a-tab-pane :key="i" :tab="m">
          <list :app-module="m"></list>
        </a-tab-pane>
      </template>
      <a-button @click="$refs.form.open()" slot="tabBarExtraContent" type="default" icon="plus">添加</a-button>
    </a-tabs>
    <template v-else>
      <a-button @click="$refs.form.open()" slot="tabBarExtraContent" type="primary" icon="plus">添加数据字典</a-button>
    </template>
    <diboot-form ref="form" @complete="formComplete"></diboot-form>
  </div>
</template>
<script>
import { dibootApi } from '@/utils/request'
import list from './list'
import dibootForm from './form'

export default {
  name: 'DictionaryIndex',
  data () {
    return {
      moduleList: []
    }
  },
  methods: {
    async initModuleList () {
      const res = await dibootApi.get('/auth-server/dictionary/moduleList')
      if (res.code === 0) {
        this.moduleList = res.data
      }
    },
    formComplete () {
      this.initModuleList()
    }
  },
  components: {
    list,
    dibootForm
  },
  created () {
    this.initModuleList()
  }

}
</script>
