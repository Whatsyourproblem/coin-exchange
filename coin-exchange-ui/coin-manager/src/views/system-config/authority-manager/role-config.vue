<template>
<div>
  <el-tree
  :data="treeData"
  show-checkbox
  default-expand-all
  node-key="id"
  ref="tree"
  highlight-current
  :props="props">
  <template v-if="data.privileges.length>0">
    <el-checkbox @change="handleCheckedChange(child)" v-model="perm.checked" v-for="perm in node.privileges" :key="perm.id" :label="perm.description"></el-checkbox>
  </template>
</el-tree>
</div>
</template>

<script>
  import {sysConfigApi} from "../../../api/sysConfigApi";

  export default {
    data() {
      return {
        treeData:[],
        props: {
          label: 'name',
          children: 'childs',
        },
        count: 1
      };
    },
    async mounted() {
      let that = this;
      let id = this.$route.params.id;
      this.id = id;
      await sysConfigApi.getRolePrivileges(id).then(function (data) {
          console.log(data)
          that.treeData = data;
      });
      // console.log(that.countPrivilegeIds, that.privilegeIds);
    },
    methods: {

    }
  };
</script>

<style scoped>


</style>
