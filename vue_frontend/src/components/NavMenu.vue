<template>
  <el-menu
    :default-active="'/index'"
    router
    mode="horizontal"
    background-color="white"
    text-color="#222"
    active-text-color="red"
    style="min-width: 100%"
  >
    <el-menu-item v-for="(item,i) in navList" :key="i" :index="item.name">{{ item.navItem }}</el-menu-item>
    <a href="#nowhere" style="color: #222;float: right;padding: 20px;">更多功能</a>
    <i class="el-icon-menu" style="float:right;font-size: 35px;color: #222;margin: auto auto"></i>
    <span
      style="position: absolute;padding-top: 20px;right: 43%;font-size: 20px;font-weight: bold"
    >White Jotter - Your Questionnair</span>
  </el-menu>
</template>

<script>
export default {
  name: "NavMenu",
  data() {
    return {
      navList: [
        { name: "/questionnaires", navItem: "问卷中心" },
        { name: "/statistic", navItem: "问卷统计" },
        { name: "/admin", navItem: "个人中心" }
      ]
    };
  }
};
</script>

<style scoped>
a {
  text-decoration: none;
}

span {
  pointer-events: none;
}
</style>
