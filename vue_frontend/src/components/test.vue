<template>
  <div>
    <!--布局容器-->
    <el-container style="background: crimson;height: 100%;min-height:100vh;" direction="vertical">
      <!--顶部栏-->
      <el-header>顶部栏</el-header>
      <el-container>
        <!--左侧侧边栏-->
        <el-aside style="background: olivedrab">左侧侧边栏</el-aside>
        <!--主要区域容器-->
        <el-main style="background: aqua">
          <el-row>
            <!--<search-bar></search-bar>-->
            <el-tooltip effect="dark" placement="right" v-for="item in books" :key="item.id">
              <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
              <p slot="content" style="font-size: 13px;margin-bottom: 6px">
                <span>{{item.author}}</span> /
                <span>{{item.date}}</span> /
                <span>{{item.press}}</span>
              </p>
              <p slot="content" style="width: 300px" class="abstract">{{item.abs}}</p>
              <el-card
                style="width: 135px;margin-bottom: 20px;height: 233px;margin-right: 15px"
                class="book"
                bodyStyle="padding:10px"
                shadow="hover"
              >
                <div class="cover">
                  <img :src="item.cover" alt="封面" />
                </div>
                <div class="info">
                  <div class="title">
                    <a href>{{item.title}}</a>
                  </div>
                </div>
                <div class="author">{{item.author}}</div>
              </el-card>
            </el-tooltip>
          </el-row>
          <el-row>
            <el-pagination :current-page="1" :page-size="10" :total="20"></el-pagination>
          </el-row>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script type="text/ecmascript-6">
// import ElAside from "element-ui/packages/aside/src/main";
// import ElMain from "element-ui/packages/main/src/main";

export default {
  // components: {
  //   ElMain,
  //   ElAside},
  name: "test",
 data() {
    return {
      books: [
        {
          cover: "https://i.loli.net/2019/04/10/5cada7e73d601.jpg",
          title: "三体",
          author: "刘慈欣",
          date: "2019-05-05",
          press: "重庆出版社",
          abs:
            "文化大革命如火如荼进行的同时。军方探寻外星文明的绝秘计划“红岸工程”取得了突破性进展。但在按下发射键的那一刻，历经劫难的叶文洁没有意识到，她彻底改变了人类的命运。地球文明向宇宙发出的第一声啼鸣，以太阳为中心，以光速向宇宙深处飞驰……"
        }
      ]
    };
  },
  methods: {}
};
</script>
