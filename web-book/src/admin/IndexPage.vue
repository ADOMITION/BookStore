<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <el-aside :width="aside_width" style="height: 108.3vh; background-color: rgb(238, 241, 246); margin-left: -1px; ">
      <AsidePage :isCollapse="isCollapse"></AsidePage>
    </el-aside>

    <el-container style="height: 100%" >
      <el-header style="text-align: right; font-size: 12px; height: 100%; border-bottom:darkgray 2px solid;">
        <HeaderPage @doCollapse="doCollapse" :icon="icon"></HeaderPage>
      </el-header>

      <el-main style="height: 100vh; background-color: #dde2ef">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import AsidePage from "@/admin/AsidePage.vue";
import HeaderPage from "@/admin/HeaderPage.vue";
export default {
  name: "Indexpage",
  components: { HeaderPage, AsidePage},
  data(){
    return {
      isCollapse: false,
      aside_width:'200px',
      icon:'el-icon-s-fold',
    }
  },
  methods:{
    doCollapse(){
      console.log(1111)
      //切换方式
      this.isCollapse = !this.isCollapse
      if(!this.isCollapse){//false为展开
        this.aside_width = '200px'
        this.icon = 'el-icon-s-fold'
      }else{//true为关闭，收起
        this.aside_width = '64px'
        this.icon = 'el-icon-s-unfold'
      }
    }
  }
}

</script>

<style scoped>
.el-header {
  /*background-color: #B3C0D1;*/
  color: #333;
  line-height: 60px;
}
el-main{
  padding: 5px;
}
.el-aside {
  color: #333;
}
</style>