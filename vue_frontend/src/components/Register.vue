<template>
<body id="poster">
  <el-form
    class="register-container"
    label-position="left"
    label-width="0px"
    :model="registerForm"
    :rules="rules"
    ref="registerForm"
  >
    <h3 class="register_title">用户注册</h3>
    <el-form-item prop="username">
      <el-input type="email" v-model="registerForm.username" auto-complete="off" placeholder="注册邮箱"></el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
        type="password"
        v-model="registerForm.password"
        auto-complete="off"
        placeholder="输入密码"
      ></el-input>
    </el-form-item>
    <el-form-item prop="checkpass">
      <el-input
        type="password"
        v-model="registerForm.checkpass"
        auto-complete="off"
        placeholder="再此输入密码"
      ></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button
        type="primary"
        style="width: 100%;background: #505458;border: none"
        v-on:click="register('registerForm')"
      >注册</el-button>
    </el-form-item>
  </el-form>
</body>
</template>

<script>
export default {
  name: "Register",
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else {
        if (value !== this.registerForm.password) {
          callback(new Error("两次输入密码不一致"));
        }
        callback();
      }
    };
    return {
      registerForm: {
        username: "",
        password: "",
        checkpass: ""
      },
      responseResult: [],
      rules: {
        username: [
          { required: true, message: "请输入注册邮箱", trigger: "blur" },
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
        ],
        checkpass: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          {
            min: 8,
            max: 15,
            message: "长度在 8 到 15 个字符",
            trigger: "blur"
          },
          { validator: validatePass, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    register(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.axios
            .post("/register", {
              username: this.registerForm.username,
              password: this.registerForm.password
            })
            .then(successResponse => {
              if (successResponse.data.code === 200) {
                   this.$message({
                  showClose: true,
                  message: "注册成功",
                  type: "success"
                });
                window.localStorage["Authorization"] =
                  successResponse.data.token;
                this.$router.push("index");
              } else {
                this.$message({
                  showClose: true,
                  message: "注册邮箱或密码不符合要求",
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
.register-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.register_title {
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