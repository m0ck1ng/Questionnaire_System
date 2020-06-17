<template>
  <div class="app-container">
    <el-row style="height: 840px;">
      <el-tooltip
        v-for="item in forms"
        :key="item.id"
        effect="dark"
        placement="right"
      >
        <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{ item.title }}</p>
        <p slot="content" style="font-size: 13px;margin-bottom: 6px">
          <span>截止时间：{{ item.due_time }}</span><br>
          <span>状态：{{ item.state }}</span>
        </p>
        <p slot="content" style="width: 300px" class="abstract">{{ item.desc }}</p>
        <el-card
          style="width: 135px;margin-bottom: 20px;height: 210px;float: left;"
          :style="{ marginLeft: computed_margin, marginRight : computed_margin }"
          class="book"
          body-style="padding:10px"
          shadow="hover"
        >
          <div class="cover">
            <img src="https://pic.17qq.com/uploads/ihdfbjeibz.jpeg" alt="封面">
          </div>
          <div class="info">
            <div class="title">
              <label>{{ item.title }}</label>
            </div>
          </div>
          <el-link v-if="item.state=='未发布'" type="primary" @click="setReleaseSignal(item.qid)">发布</el-link>
          <el-link v-if="item.state!='未发布'" type="success" @click="showURL(item.qid, item.title)">分享</el-link>
          <el-link v-if="item.state != '未发布'" type="primary" :href="'#/statistics/'+item.qid">统计</el-link>
          <el-link type="danger" @click="delForm(item.qid)">删除</el-link>
        </el-card>

      </el-tooltip>
      <el-dialog :visible.sync="dialogReleaseVisible" :width="dialog_window_width">
        <div slot="title" align="left">
          <span style="font-weight: bold">设置填写方式</span>
        </div>
        <el-form ref="formstate" :rules="rules" :model="formstate">
          <el-form-item label="填写设置" prop="state">
            <el-select v-model="formstate.state">
              <el-option key="1" value="REONLY" label="仅限注册用户">仅限注册用户</el-option>
              <el-option key="2" value="NMOST" label="无需注册，可填写n次">无需注册，可填写n次</el-option>
              <el-option key="3" value="DMOST" label="无需注册，每天可填写n次">无需注册，每天可填写n次</el-option>
            </el-select>
          </el-form-item>

          <el-form-item v-if="formstate.state == 'NMOST' || formstate.state == 'DMOST'" label="最大填写次数" prop="max_time">
            <el-input-number v-model="formstate.max_time" min="1" max="10" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button size="small" @click="dialogReleaseVisible = false">取消</el-button>
          <el-button type="primary" size="small" @click="release('formstate')">确定</el-button>
        </div>
      </el-dialog>
    </el-row>
  </div>
</template>

<script>
import { getAllForms, deleteForm, releaseForm } from '@/api/form'
import { getToken } from '@/utils/auth'
export default {
  name: 'Books',
  data() {
    return {
      forms: [],
      formstate: {
        state: '',
        max_time: 1
      },
      dialogReleaseVisible: false,
      target_qid: '',
      rules: {
        state: [{ required: true, message: '设置填写方式', trigger: 'blur' }],
        max_time: [{ required: true, message: '设置最大填写次数', trigger: 'change' }]
      }
    }
  },
  computed: {
    computed_margin: function() {
      var width = document.body.clientWidth
      var card_width = 155
      var margin = 15
      var maxnum = parseInt(width / card_width)
      if (maxnum > 5) { maxnum = 5 }
      margin = Math.max(parseInt((width - card_width * maxnum) / (2 * maxnum)), 15)
      return margin + 'px'
    },
    dialog_window_width: function() {
      var max_width = 600
      var available_width = document.body.clientWidth * 0.9
      if (available_width < max_width) { max_width = available_width }
      return max_width + 'px'
    }
  },
  created: function() {
    var token = getToken()
    getAllForms(token).then(response => {
      this.forms = response.data
    })
  },
  methods: {
    delForm(qid) {
      var token = getToken()
      deleteForm(qid, token).then(response => {
        this.$message.success(response.message)
        getAllForms(token).then(response => {
          this.forms = response.data
        })
      })
    },
    setReleaseSignal(qid) {
      this.dialogReleaseVisible = true
      this.target_qid = qid
    },
    get_Token() {
      return getToken()
    },
    showURL(qid, title) {
      var url = 'http://localhost:9526/#/shared/' + qid
      this.$alert(url, title + '分享链接', {
        confirmButtonText: '确定'
      })
    },
    release(form) {
      var token = getToken()
      this.$refs[form].validate(valid => {
        if (valid) {
          releaseForm(this.target_qid, token, this.formstate.state, this.formstate.max_time).then(response => {
            this.$message.success(response.message)
            this.formstate.state = ''
            this.formstate.max_time = 1
            this.dialogReleaseVisible = false
            getAllForms(token).then(response => {
              this.forms = response.data
            })
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

<style scoped>
  .cover {
    width: 115px;
    height: 120px;
    margin-bottom: 7px;
    overflow: hidden;
    cursor: pointer;
  }

  img {
    width: 115px;
    height: 120px;
    /*margin: 0 auto;*/
  }

  .title {
    font-size: 14px;
    text-align: left;
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

  a:link, a:visited, a:focus {
    color: #3377aa;
  }
  .info {
    margin-bottom: 8px;
  }
</style>

