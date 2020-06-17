<template>
  <div class="app-container">
    <el-form v-if="showForm" ref="answers" :model="answers" status-icon :label-width="label_width">
      <h1 class="form-head">{{ form_template.title }}</h1>
      <p class="form-head">{{ form_template.desc }}</p>

      <div v-for="(Ques, index) in getQueslist()" :key="Ques.key">
        <div class="ques-part" style="text-align:left">
          <el-form-item
            v-if="isPrevExist(Ques.pre)"
            :label="'问题'+(index+1)"
            :required="Ques.required"
          >
            <label>{{ Ques.desc }}</label>
            <div>
              <!-- 文本类型-->
              <el-input
                v-if="Ques.type === 'input' || Ques.type === 'textarea'"
                v-model="answers[index]"
                class="answer"
                :type="Ques.type"
              />
              <el-input-number v-else-if="Ques.type === 'num'" v-model="answers[index]" class="answer" type="input" />
              <!-- 单选/多选类型-->
              <div v-else-if="Ques.type === 'checkbox'">
                <el-select
                  v-model="answers[index]"
                  class="answer"
                  multiple
                  :multiple-limit="Ques.options.ansNum"
                  placeholder="请选择"
                >
                  <el-option
                    v-for="item in Ques.options.values"
                    :key="item.key"
                    :label="item.label"
                    :value="item.key"
                  />
                </el-select>
              </div>
              <div v-else-if="Ques.type === 'radio'">
                <el-select v-model="answers[index]" placeholder="请选择" class="answer">
                  <el-option
                    v-for="item in Ques.options.values"
                    :key="item.key"
                    :label="item.label"
                    :value="item.key"
                  />
                </el-select>
              </div>
              <!-- 打分类型-->
              <el-rate v-else-if="Ques.type == 'rate'" v-model="answers[index]" class="answer" />
              <el-input
                v-else-if="Ques.type == 'pos'"
                v-model="answers[index]"
                class="answer"
                placeholder="点击获取位置信息"
                readonly="true"
                @click.native="getLocalPos(index)"
              />
            </div>
          </el-form-item>
        </div>
      </div>

      <el-form-item>
        <el-button type="primary" @click="submit('answers')">提交</el-button>
      </el-form-item>

    </el-form>
    <h2 v-if="showThanks" class="form-head">感谢填写</h2>
  </div>
</template>

<style>
@import url("//unpkg.com/element-ui@2.13.0/lib/theme-chalk/index.css");
</style>
<script>
import { queryForm } from '@/api/form'
import { addReply } from '@/api/reply'
// eslint-disable-next-line
import AMap from 'AMap'
// eslint-disable-next-line no-extend-native
Date.prototype.format = function(fmt) {
  var o = {
    'M+': this.getMonth() + 1, // 月份
    'd+': this.getDate(), // 日
    'h+': this.getHours(), // 小时
    'm+': this.getMinutes(), // 分
    's+': this.getSeconds(), // 秒
    'q+': Math.floor((this.getMonth() + 3) / 3), // 季度
    'S': this.getMilliseconds() // 毫秒
  }
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length))
  }
  for (var k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)))
    }
  }
  return fmt
}
export default {
  data() {
    return {
      form_template: '',
      answers: [],
      show_indices: [],
      showForm: false,
      showThanks: false,
      city: ''
    }
  },
  computed: {
    label_width: function() {
      var width
      if (document.body.clientWidth > 600) { width = document.body.clientWidth * 0.35 } else { width = document.body.clientWidth * 0.23 }
      return width + 'px'
    }
  },
  created: function() {
    var id = this.$route.params.id
    queryForm(id).then(response => {
      this.form_template = response.form
      this.answers = new Array(this.form_template.Qslist.length)
      this.showForm = true
    })
  },
  methods: {
    getLocalPos(index) {
      var self = this
      AMap.plugin('AMap.Geolocation', function() {
        var geolocation = new AMap.Geolocation({
          enableHighAccuracy: true,
          timeout: 10000
        })

        geolocation.getCurrentPosition()
        AMap.event.addListener(geolocation, 'complete', onComplete)
        AMap.event.addListener(geolocation, 'error', onError)

        function onComplete(data) {
          var loc = data.addressComponent.province + ' ' + data.addressComponent.city + ' ' + data.addressComponent.district
          self.$set(self.answers, index, loc)
        }

        function onError(data) {
          console.log('定位失败', data)
          self.answers[index] = '定位获取失败'
        }
      })
    },
    submit(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          var datetime = (new Date()).format('yyyy-MM-dd hh:mm:ss')
          var ordered_ans = new Array(this.answers.length)
          for (var index in this.show_indices) {
            var real_index = this.show_indices[index]
            ordered_ans[real_index] = this.answers[index]
          }
          var data = {
            ans: ordered_ans,
            time: datetime,
            qid: this.$route.params.id
          }
          addReply(data).then(response => {
            this.$message.success(response.message)
            this.showForm = false
            this.$refs[form].resetFields()
            this.showThanks = true
          })
        } else {
          this.$message.warning({
            showClose: true,
            message: '请按提示输入'
          })
        }
      })
    },
    findQuesIndexWithKey(key) {
      for (var index in this.form_template.Qslist) {
        if (this.form_template.Qslist[index].key === parseInt(key)) { return index }
      }
      return -1
    },
    isPrevExist(pre) {
      if (pre.key === '') { return true }
      var index = this.findQuesIndexWithKey(pre.key)
      if (index === -1) { return false }
      var pre_val = this.answers[index]
      if (pre.type === 'radio' || pre.type === 'checkbox') {
        if (typeof (pre_val) === 'undefined') { return false }
        var a = pre_val.toString()
        var b = pre.options.toString()
        return (a.indexOf(b) !== -1)
      }
    },
    getQueslist() {
      var showlist = []
      var showlist_indice = []
      for (var index in this.form_template.Qslist) {
        if (this.isPrevExist(this.form_template.Qslist[index].pre)) {
          showlist.push(this.form_template.Qslist[index])
          showlist_indice.push(index)
        }
      }
      this.show_indices = showlist_indice
      return showlist
    }
  }
}
</script>

<style>
.form-head {
  text-align: center;
}

.answer {
  width: 45%;
  min-width: 200px;
}
</style>

