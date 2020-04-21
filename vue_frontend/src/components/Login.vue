<template>
<body id="poster">
  <el-form
    class="login-container"
    label-position="left"
    label-width="0px"
    :model="loginForm"
    :rules="rules"
    ref="loginForm"
  >
    <h3 class="login_title">系统登录</h3>
    <el-form-item prop="username">
      <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="邮箱"></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button
        type="primary"
        style="width: 100%;background: #505458;border: none"
        v-on:click="login('loginForm')"
      >登录</el-button>
    </el-form-item>
  </el-form>
</body>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        username: "",
        password: ""
      },
      responseResult: [],
      rules: {
        username: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          {
            min: 6,
            max: 30,
            message: "长度在 6 到 30 个字符",
            trigger: "blur"
          },
          { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" }
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 8, max: 15, message: "长度在 8 到 15 个字符", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    login(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.axios
            .post("/login", {
              username: this.loginForm.username,
              password: this.loginForm.password
            })
            .then(successResponse => {
              if (successResponse.data.code === 200) {
                this.$message({
                  showClose: true,
                  message: "登录成功",
                  type: "success"
                });
                window.localStorage["Authorization"] =
                  successResponse.data.token;
                this.$router.push("index");
              } else {
                this.$message({
                  showClose: true,
                  message: "账号或密码错误",
                  type: "error"
                });
              }
            })
            .catch(failResponse => {
              alert("连接失败");
            });
        } else {
          this.$message({
            showClose: true,
            message: "请按提示输入",
            type: "error"
          });
        }
      });
    }
  }
};
</script>

<style>
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}

#poster {
  background: url("../assets/login-bg.jpg") no-repeat;
  background-position: center;
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
  margin: -8px -8px;
}
</style>