<template>
  <el-container style="height: 100%" direction="vertical">
    <el-container>
      <el-aside style="width: 200px;margin-top: 20px">
        <el-menu
          class="categories"
          default-active="0"
          @select="handleSelect"
          active-text-color="red"
        >
          <el-menu-item index="0">
            <i class="el-icon-menu"></i>
            <span slot="title">全部问卷</span>
          </el-menu-item>
          <el-menu-item index="1">
            <i class="el-icon-menu"></i>
            <span slot="title">已发布</span>
          </el-menu-item>
          <el-menu-item index="2">
            <i class="el-icon-menu"></i>
            <span slot="title">未发布</span>
          </el-menu-item>
          <el-menu-item index="3">
            <i class="el-icon-menu"></i>
            <span slot="title">新建问卷</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main style="height: 100%">
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
        <el-row style="margin: 10% auto">
          <el-pagination :current-page="1" :page-size="10" :total="20"></el-pagination>
        </el-row>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "Questionnaires",
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
  }
};
</script>

<style scoped>
.cover {
  width: 100%;
  height: 100%;
  margin-bottom: 7px;
  overflow: hidden;
  cursor: pointer;
}
.categories {
  position: fixed;
  margin-left: 50%;
  left: -600px;
  top: 100px;
  width: 150px;
}

img {
  width: 100%;
  height: 100%;
  /*margin: 0 auto;*/
}

.title {
  font-size: 14px;
  text-align: center;
}

.author {
  color: #333;
  width: 102px;
  font-size: 13px;
  margin-bottom: 6px;
  text-align: left;
}

.abstract {
  display: block;
  line-height: 17px;
}

a {
  text-decoration: none;
}

a:link,
a:visited,
a:focus {
  color: #3377aa;
}

html,
body,
.el-container {
  /*设置内部填充为0，几个布局元素之间没有间距*/
  padding: 0px;
  /*外部间距也是如此设置*/
  margin: 0px;
  /*统一设置高度为100%*/
  height: 100%;
}
</style>