import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import axios from "axios";
import VueRouter from "vue-router";
import router from "./router/router";


Vue.config.productionTip = false
Vue.prototype.$axios=axios;
Vue.prototype.$httpUrl='http://localhost:8090'
axios.defaults.withCredentials = true;// 允许携带cookie


//

Vue.use(VueRouter);
//设置页面小一点
Vue.use(ElementUI,{size:'small'});
new Vue({
  router,
  render: h => h(App),
}).$mount('#app')