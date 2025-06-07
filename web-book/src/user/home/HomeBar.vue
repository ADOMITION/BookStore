<template>
  <div>
    <div>
      <el-carousel height="530px"
                   indicator-position="outside" style="margin-top:-50px;">
        <el-carousel-item v-for="item in pics" :key="item.url">
          <img :src="item.url" alt=""/>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div>
      <nva-bar/>
    </div>
  </div>
</template>

<script>
import NvaBar from "@/user/home/NvaBar.vue";
export default {
  name: "Home",
  data(){
    return{
      pics: [
        { url: require("./img/carousel-1.jpg") },
        { url: require("./img/carousel-2.jpg") },
        { url: require("./img/carousel-3.jpg") },
        { url: require("./img/carousel-4.jpg") },
        { url: require("./img/carousel-5.jpg") }
      ]
    }
  },
  methods: {
  },
  components:{NvaBar}
}
</script>

<style scoped>

</style>