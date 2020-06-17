<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" :label-width="label_width">
      <el-form-item>
        <label class="head-text">你好, {{ name }}</label>
      </el-form-item>
      <el-form-item prop="passwd" label="请输入原密码">
        <el-input v-model="form.passwd" type="password" :style="{ width: input_width}" />
      </el-form-item>
      <el-form-item prop="new_passwd" label="请输入新密码">
        <el-input v-model="form.new_passwd" type="password" :style="{ width: input_width}" />
      </el-form-item>
      <el-form-item prop="new_passwd_again" label="请再次输入新密码">
        <el-input v-model="form.new_passwd_again" type="password" :style="{ width: input_width}" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit('form')">修改</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { modify } from '@/api/user'
import { getToken } from '../../utils/auth'
export default {
  name: 'User',
  data() {
    const validateCheckPass = (rule, value, callback) => {
      if (value !== this.form.new_passwd) {
        callback(new Error('两次输入密码不一致'))
      } else {
        console.log(value, this.form.new_passwd)
        callback()
      }
    }
    return {
      form: {
        new_passwd: '',
        passwd: '',
        new_passwd_again: ''
      },
      rules: {
        passwd: [{ required: true, trigger: 'blur', message: '请输入密码' },
          { min: 6, max: 30, message: '密码长度不小于6个字符,不大于30个字符' }],
        new_passwd: [{ required: true, trigger: 'blur', message: '请输入密码' },
          { min: 6, max: 30, message: '密码长度不小于6个字符,不大于30个字符' }],
        new_passwd_again: [{ required: true, trigger: 'blur', validator: validateCheckPass }]
      }
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ]),
    label_width: function() {
      var min_width = 80
      var available_width = document.body.clientWidth * 0.15
      if (available_width > min_width) { min_width = available_width }
      return min_width + 'px'
    },
    input_width: function() {
      var max_width = 600
      var available_width = document.body.clientWidth * 0.8
      if (available_width < max_width) { max_width = available_width }
      return max_width + 'px'
    }
  },
  methods: {
    submit(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          modify({
            origin_passwd: this.form.passwd,
            new_passwd: this.form.new_passwd,
            token: getToken()
          }).then(response => {
            this.$message.success(response.message)
            this.$refs[form].resetFields()
          })
        } else {
          this.$message.warning({
            showClose: true,
            message: '请按提示输入'
          })
        }
      })
    }
  }
}
</script>
<style>
.head-text
{
    font-size: 20px;
    line-height: 30px;
}
</style>
