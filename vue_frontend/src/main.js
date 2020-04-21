// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import { Rate, Menu, Submenu, Main,Aside, Footer,Header,Container,MenuItem,Col, Row, Dialog, 
  Dropdown, DropdownMenu, DropdownItem, FormItem, Form, Button, Select, Input, Card, Option, 
  Checkbox, RadioGroup, CheckboxGroup, Radio, Icon, Tooltip, Popover, Pagination, Message } from 'element-ui';
import App from './App'
import router from './router'
import axios from 'axios'
import VueAxios from 'vue-axios'

Vue.config.productionTip = false
Vue.use(Input)
Vue.use(Button)
Vue.use(Select)
Vue.use(Option)
Vue.use(Card)
Vue.use(Radio)
Vue.use(RadioGroup)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Icon)
Vue.use(Tooltip)
Vue.use(Popover)
Vue.use(Rate)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Col)
Vue.use(Dropdown)
Vue.use(DropdownItem)
Vue.use(DropdownMenu)
Vue.use(Dialog)
Vue.use(Row)
Vue.use(VueAxios, axios);
Vue.use(Menu)
Vue.use(Submenu)
Vue.use(MenuItem)
Vue.use(Container)
Vue.use(Header)
Vue.use(Aside)
Vue.use(Footer)
Vue.use(Tooltip)
Vue.use(Main)
Vue.use(Pagination)
Vue.component(Message)
Vue.prototype.$message = Message

axios.defaults.baseURL = 'http://localhost:8443/api'
// http request 拦截器
axios.interceptors.request.use(
  request => {
    if (localStorage.getItem("Authorization")) { //判断token是否存在
      request.headers["Authorization"] = localStorage.getItem("Authorization");  //将token设置成请求头
    }
    return request;
  },
  err => {
    return Promise.reject(err);
  }
);

// http response 拦截器
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    return Promise.reject(error.response)
  }
);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
