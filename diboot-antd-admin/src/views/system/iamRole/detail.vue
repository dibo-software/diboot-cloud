<template>
  <a-drawer
    :title="`角色${title}`"
    :width="width"
    @close="close"
    :visible="visible"
    :body-style="{ paddingBottom: '80px' }"
  >
    <a-spin :spinning="spinning">
      <a-descriptions :column="1">
        <a-descriptions-item label="角色名称">{{ model.name }}</a-descriptions-item>
        <a-descriptions-item label="角色编码">{{ model.code }}</a-descriptions-item>
        <a-descriptions-item label="角色描述">{{ model.description || '-' }}</a-descriptions-item>
        <a-descriptions-item label="已授权权限">
          <a-tree
            v-if="permissionTreeList.length > 0"
            showIcon
            checkStrictly
            :defaultExpandAll="true"
            :treeData="permissionTreeList"
          >
            <a-icon slot="menu" type="bars" />
            <a-icon slot="permission" type="thunderbolt" />
          </a-tree>
          <template v-else>
            无
          </template>
        </a-descriptions-item>
      </a-descriptions>
    </a-spin>

    <div class="drawer-footer">
      <a-button :style="{marginRight: '8px'}" @click="close">关闭</a-button>
    </div>
  </a-drawer>
</template>

<script>
import detail from '@/components/diboot/mixins/detail'
import { permissionTreeListFormatter } from '@/utils/treeDataUtil'

export default {
  name: 'IamRoleDetail',
  data () {
    return {
      baseApi: '/auth-server/iam/role',
      permissionTreeList: []
    }
  },
  methods: {
    async afterOpen (id) {
      if (this.model && this.model.permissionVOList) {
        this.permissionTreeList = permissionTreeListFormatter(this.model.permissionVOList, 'id', 'displayName')
      }
    },
    afterClose () {
      this.permissionTreeList = []
    }
  },
  mixins: [ detail ]
}
</script>

<style lang="less" scoped>
</style>
