<template>
  <div class="app-container">
    <el-form ref="Qform" :model="Qform" :rules="rules" :label-width="label_width">

      <el-form-item prop="title" label="问卷标题">
        <el-input v-model="Qform.title" placeholder="输入问卷标题" />
      </el-form-item>

      <el-form-item prop="desc" label="问卷说明">
        <el-input v-model="Qform.desc" type="textarea" placeholder="输入问卷说明" />
      </el-form-item>

      <!--Due Time-->
      <el-form-item label="截止时间">
        <el-date-picker v-model="datetime" :picker-options="pickerOpt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" />
      </el-form-item>

      <transition-group name="fade" tag="div">
        <div v-for="(Ques, cindex) in Qform.Qslist" :key="Ques.key">
          <el-form-item :label="'问题 '+(cindex+1)">
            <el-card class="box-card" shadow="hover">
              <!--卡片头部用于输入问题描述与问题类型 -->
              <!--输入问题描述 -->
              <div slot="header" class="clearfix">
                <el-row type="flex" class="row-bg" justify="space-around">
                  <el-col :span="13">
                    <el-form-item
                      :prop="'Qslist.'+cindex+'.desc'"
                      label-width="0"
                      :rules="[
                        { required: true, message: '输入问题描述', trigger: 'blur' },
                        {min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: ['blur', 'change'] }
                      ]"
                    >
                      <el-input v-model="Ques.desc" class="self-fill-input" placeholder="输入问题描述" size="medium" />
                    </el-form-item>
                  </el-col>
                  <!--选择问题类型 -->
                  <el-col :span="9" offset="2">
                    <el-form-item prop="type" style="margin: 0 0px" label-width="0">
                      <el-select
                        v-model="Ques.type"
                        size="medium"
                        @change="changeType(cindex)"
                      >
                        <el-option
                          v-for="item in types"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
              </div>

              <!-- 卡片选项部分用于输入问题的回答选项-->
              <div class="options-part" style="text-align:left">
                <!-- 文本类型-->
                <el-input
                  v-if="Ques.type === 'input' || Ques.type === 'textarea'"
                  :disabled="true"
                  :type="Ques.type"
                  placeholder="待填写者输入"
                />
                <el-input
                  v-if="Ques.type === 'num' || Ques.type === 'pos'"
                  :disabled="true"
                  type="input"
                  placeholder="待填写者输入"
                />
                <!-- 单选/多选类型-->
                <div
                  v-else-if="Ques.type === 'radio' || Ques.type ==='checkbox'"
                  class="qs-text-radio-wrap"
                >
                  <el-form-item v-if="Ques.type === 'checkbox'" label="最多可选" label-width="70px">
                    <el-select v-model="Qform.Qslist[cindex].options.ansNum" size="medium" placeholder="请选择">
                      <el-option
                        v-for="i in Qform.Qslist[cindex].options.values.length"
                        :key="i"
                        :label="i"
                        :value="i"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-for="(item, oindex) in Qform.Qslist[cindex].options.values"
                    :key="item.key"
                    class="opt-value-fill"
                    label-width="0px"
                    :prop="'Qslist.'+cindex+'.options.values.'+oindex+'.label'"
                    :rules="[
                      { required: true, message: '输入选项值', trigger: 'blur' },
                      {max: 30, message: '长度在 1 到 30 个字符', trigger: ['blur', 'change'] }
                    ]"
                  >
                    <el-input
                      v-model="item.label"
                      :placeholder="'选项'+(oindex+1)"
                      class="self-fill-input"
                      size="medium"
                      style="width: 90%"
                      @focus="deleteVisibleSign = cindex*100+oindex"
                    />
                    <i
                      v-show="deleteVisibleSign == cindex*100+oindex"
                      class="el-icon-remove"
                      @click="deleteOption(cindex, oindex)"
                    />
                  </el-form-item>
                </div>
                <!-- 打分类型-->
                <el-rate v-else-if="Ques.type == 'rate'" disabled />
              </div>
              <!-- 卡片底部用于增删选项-->
              <el-col>
                <!-- 添加单选和多选选项的按钮-->
                <el-tooltip effect="dark" content="添加选项" placement="top">
                  <el-button
                    v-if="Ques.type === 'radio' || Ques.type === 'checkbox'"
                    type="primary"
                    icon="el-icon-plus"
                    size="mini"
                    circle
                    class="foot-icon-button"
                    @click="addOption(cindex)"
                  />
                </el-tooltip>

                <!-- 添加题目的级联操作-->
                <el-tooltip effect="dark" content="添加关联题目" placement="top">
                  <el-button
                    type="primary"
                    icon="el-icon-sort"
                    size="mini"
                    circle
                    class="foot-icon-button"
                    @click="dialogFormVisible = Ques.key"
                  />
                </el-tooltip>
                <!-- 设置问题必填与否的按钮-->
                <el-tooltip effect="dark" content="设为必填" placement="top">
                  <el-button
                    v-show="!Ques.required"
                    type="primary"
                    icon="el-icon-star-off"
                    size="mini"
                    circle
                    class="foot-icon-button"
                    @click="Ques.required = !Ques.required"
                  />
                </el-tooltip>
                <el-tooltip effect="dark" content="取消必填" placement="top">
                  <el-button
                    v-show="Ques.required"
                    type="primary"
                    icon="el-icon-star-on"
                    size="mini"
                    circle
                    class="foot-icon-button"
                    @click="Ques.required = !Ques.required"
                  />
                </el-tooltip>
                <!-- 添加问题或者拷贝问题的按钮-->
                <el-dropdown @command="handleCommand">
                  <el-button type="info" class="foot-icon-button" icon="el-icon-document-copy" size="mini" circle />
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="composeCommand('add', cindex)">添加新问题</el-dropdown-item>
                    <el-dropdown-item :command="composeCommand('copy', cindex)">拷贝该问题</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <!-- 删除当前问题的按钮-->
                <el-tooltip effect="dark" content="删除问题" placement="top">
                  <el-button
                    type="danger"
                    icon="el-icon-delete"
                    size="mini"
                    class="foot-icon-button"
                    circle
                    @click="deleteQues(cindex)"
                  />
                </el-tooltip>
              </el-col>
            </el-card>
          </el-form-item>
          <el-dialog :visible.sync="dialogFormVisible == Ques.key" :width="dialog_window_width">
            <div slot="title" align="left">
              <span style="font-weight: bold">添加关联题目</span>
            </div>
            <el-form-item label="1.选择触发题目" style="font-weight: bold;" label-width="120px">
              <el-select
                v-model="Ques.pre.key"
                placeholder="选择"
                size="medium"
                @change="Ques.pre.type = findQuesWithKey(parseInt(Ques.pre.key)).type"
              >
                <el-option
                  v-for="(item, qindex) in getPreviousQues(cindex)"
                  :key="item.key"
                  :label="'问题'+(qindex+1)+' '+item.desc"
                  :value="item.key.toString()"
                  :disabled="isValid(item)"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="Ques.pre.key != '' && (Ques.pre.type == 'text' || Ques.pre.type == 'textarea')"
              label="输入触发值"
              style="font-weight: bold;"
              :label-width="label_width"
            >
              <el-input v-model="Ques.pre.val" placeholder="输入" />
            </el-form-item>
            <el-form-item
              v-else-if="Ques.pre.key != '' && (Ques.pre.type == 'radio' || Ques.pre.type == 'checkbox')"
              label="选择触发选项"
              style="font-weight: bold"
              label-width="120px"
            >
              <el-select v-model="Ques.pre.options" multiple placeholder="选择">
                <el-option
                  v-for="(item, index) in findQuesWithKey(parseInt(Ques.pre.key)).options.values"
                  :key="item.key"
                  :label="'选项'+(index+1)+' '+item.label"
                  :value="item.key"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="2.选择跳转题目" style="font-weight: bold" label-width="120px">
              <el-select v-model="Ques.next" placeholder="选择" size="medium">
                <el-option
                  v-for="(item,qindex) in getRearQues(cindex)"
                  :key="item.key"
                  :label="'问题'+(qindex+cindex+2)+' '+item.desc"
                  :value="item.key"
                  :disabled="isValid(item)"
                />
              </el-select>
            </el-form-item>
            <div slot="footer" class="dialog-footer">
              <el-button size="small" @click="clearLogic(cindex)">取消</el-button>
              <el-button type="primary" size="small" @click="dialogFormVisible = -1">确定</el-button>
            </div>
          </el-dialog>
        </div>
      </transition-group>
      <el-form-item>
        <el-button type="primary" @click="dialogReleaseVisible = !dialogReleaseVisible">发布</el-button>
        <el-button @click="submit('Qform', 'SAVED')">保存</el-button>
        <el-button type="danger" @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
    <el-dialog :visible.sync="dialogReleaseVisible" :width="dialog_window_width">
      <div slot="title" align="left">
        <span style="font-weight: bold">设置填写方式</span>
      </div>
      <el-form>
        <el-form-item
          label="填写设置"
          prop="state"
        >
          <el-select v-model="state">
            <el-option key="1" value="REONLY" label="仅限注册用户">仅限注册用户</el-option>
            <el-option key="2" value="NMOST" label="无需注册，可填写n次">无需注册，可填写n次</el-option>
            <el-option key="3" value="DMOST" label="无需注册，每天可填写n次">无需注册，每天可填写n次</el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="state == 'NMOST' || state == 'DMOST'"
          label="最大填写次数"
          prop="max_time"
        >
          <el-input-number v-model="max_time" min="1" max="10" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="reset('Qform')">取消</el-button>
        <el-button type="primary" size="small" @click="submit('Qform', state)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<style>
@import url("//unpkg.com/element-ui@2.13.0/lib/theme-chalk/index.css");
</style>
<script>
const state = {
  baseSet: {
    radio: {
      values: [
        { label: '', key: 1 },
        { label: '', key: 2 }
      ]
    },
    checkbox: {
      values: [
        { label: '', key: 3 },
        { label: '', key: 4 }
      ],
      ansNum: 1
    },
    input: null,
    textarea: null,
    num: null,
    pos: null
  }
}
const Qs = {
  desc: '',
  type: 'radio',
  required: true,
  options: state.baseSet['radio'],
  key: 0,
  pre: {
    key: '',
    options: [],
    type: '',
    val: ''
  },
  next: ''
}
import { addForm } from '@/api/form'
import { getToken } from '@/utils/auth'
export default {
  data() {
    return {
      Qform: {
        title: '',
        desc: '',
        Qslist: [JSON.parse(JSON.stringify(Qs))]
      },
      datetime: '',
      deleteVisibleSign: -1,
      isButtonVisible: true,
      key_counter: 5,
      state: 'REONLY',
      max_time: 1,
      dialogFormVisible: -1,
      dialogReleaseVisible: false,
      types: [
        { label: '单选', value: 'radio' },
        { label: '多选', value: 'checkbox' },
        { label: '单行文本', value: 'input' },
        { label: '多行文本', value: 'textarea' },
        { label: '评分', value: 'rate' },
        { label: '数字', value: 'num' },
        { label: '地理位置', value: 'pos' }
      ],
      rules: {
        title: [
          { required: true, message: '输入问卷名称', trigger: 'blur' },
          { min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur' }
        ],
        desc: [
          { required: true, message: '输入问卷说明', trigger: 'blur' },
          { min: 3, max: 60, message: '长度在 3 到 60 个字符', trigger: 'blur' }
        ],
        state: [
          { required: true, message: '设置填写方式', trigger: 'change' }
        ],
        max_time: [
          { required: true, message: '设置最大填写次数', trigger: 'change' }
        ]
      },
      pickerOpt: {
        disabledDate(time) {
          return time.getTime() <= Date.now()
        }
      }
    }
  },
  computed: {
    label_width: function() {
      var min_width = 80
      var available_width = document.body.clientWidth * 0.1
      if (available_width > min_width) { min_width = available_width }
      return min_width + 'px'
    },
    dialog_window_width: function() {
      var max_width = 600
      var available_width = document.body.clientWidth * 0.9
      if (available_width < max_width) { max_width = available_width }
      return max_width + 'px'
    }
  },
  methods: {
    addQues(cindex) {
      var qs = JSON.parse(JSON.stringify(Qs))
      qs.key = this.key_counter
      this.key_counter++
      this.Qform.Qslist.splice(cindex + 1, 0, qs)
    },
    deleteQues(cindex) {
      if (this.Qform.Qslist.length === 1) return
      this.isButtonVisible = false
      this.Qform.Qslist.splice(cindex, 1)
      setTimeout(() => {
        this.isButtonVisible = true
      }, 400)
    },
    copyQues(cindex) {
      var qs = JSON.parse(JSON.stringify(this.Qform.Qslist[cindex]))
      qs.key = this.key_counter
      this.key_counter++
      this.Qform.Qslist.splice(cindex + 1, 0, qs)
    },
    changeType(cindex) {
      this.Qform.Qslist[cindex].options =
        JSON.parse(JSON.stringify(state.baseSet[this.Qform.Qslist[cindex].type]))
    },
    addOption(cindex) {
      switch (this.Qform.Qslist[cindex].type) {
        case 'radio':
        case 'checkbox':
          this.Qform.Qslist[cindex].options['values'].push({
            label: '',
            key: this.key_counter
          })
          this.key_counter++
          break
      }
    },
    deleteOption(cindex, oindex) {
      if (this.Qform.Qslist[cindex].options.values.length === 1) return
      this.Qform.Qslist[cindex].options.values.splice(oindex, 1)
    },
    formateNum(num) {
      if (num < 10) return '0' + num
      return num
    },
    composeCommand(method, cindex) {
      return {
        method: method,
        index: cindex
      }
    },
    handleCommand(command) {
      if (command['method'] === 'add') this.addQues(command['index'])
      else this.copyQues(command['index'])
    },
    getPreviousQues(cindex) {
      if (cindex === 0) return []
      else return this.Qform.Qslist.slice(0, cindex)
    },
    getRearQues(cindex) {
      if (cindex === this.Qform.Qslist.length) return []
      else return this.Qform.Qslist.slice(cindex + 1, this.Qform.Qslist.length)
    },
    findQuesWithKey(key) {
      for (var index in this.Qform.Qslist) {
        if (this.Qform.Qslist[index].key === key) { return this.Qform.Qslist[index] }
      }
    },
    isValid(Ques) {
      if (Ques.desc === '') return true
      if (Ques.type === 'radio' || Ques.type === 'checkbox') {
        for (var i in Ques.options.values) { if (Ques.options.values[i].label === '') return true }
      }
      return false
    },
    clearLogic(cindex) {
      this.Qform.Qslist[cindex].pre.key = ''
      this.Qform.Qslist[cindex].next = ''
      this.dialogFormVisible = -1
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning',
        list: []
      })
    },
    reset(form) {
      this.$refs[form].resetFields()
      this.state = 'REONLY'
      this.max_time = 1
      this.dialogReleaseVisible = false
      this.key_counter = 5
      this.datetime = ''
    },
    submit(form, state) {
      this.dialogReleaseVisible = false
      this.$refs[form].validate(valid => {
        if (valid) {
          var data
          if (state === 'SAVED' || state === 'REONLY') {
            data = {
              form: this.Qform,
              state: state,
              due_time: this.datetime,
              token: getToken()
            }
          } else {
            data = {
              form: this.Qform,
              state: state,
              max_time: this.max_time,
              due_time: this.datetime,
              token: getToken()
            }
          }
          addForm(data).then(response => {
            this.$message.success(response.message)
            this.reset(form)
            this.$alert(response.url, '问卷链接', {
              confirmButtonText: '确定',
              cancelButtonText: '取消'
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
<style>
/* .text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
} */

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.el-input__icon .el-icon-remove:hover {
  visibility: "visible";
}

/* .self-fill-input /deep/ .el-input__inner {
  border-top-width: 0px;
  border-left-width: 0px;
  border-right-width: 0px;
  border-bottom-width: 1px;
  border-radius: 0;
} */
.foot-icon-button {
  margin: 10px 5px
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s;
}
.fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
  opacity: 0;
}
.fade-move {
  transform: all 5s;
}

/* #release-btn {
  position: absolute;
  right: 20%;
  bottom: 30%;
} */
</style>
