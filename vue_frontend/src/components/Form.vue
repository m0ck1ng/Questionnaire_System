<template>
  <el-form
    :model="Qform"
    status-icon
    :rules="rules"
    ref="Qform"
    label-width="80px"
    label-position="right"
  >
    <el-form-item prop="title" style="margin: 20px 33%" label="问卷标题">
      <el-input
        v-model="Qform.title"
        placeholder="请输入问卷标题"
        class="self-fill-input"
        style="width: 250px;"
      ></el-input>
    </el-form-item>
    <el-form-item prop="desc" style="margin: 20px 33%" label="问卷说明">
      <el-input
        type="textarea"
        v-model="Qform.desc"
        placeholder="请输入问卷说明"
        autosize
        style="width: 300px;"
      ></el-input>
    </el-form-item>
    <transition-group name="fade" tag="div">
      <div v-for="(Ques, cindex) in Qform.Qslist" :key="Ques.key">
        <el-card class="box-card" shadow="hover" style="margin: 0 auto">
          <!--卡片头部用于输入问题描述与问题类型 -->
          <!--输入问题描述 -->
          <div slot="header" class="clearfix">
            <el-row type="flex" class="row-bg" justify="space-around">
              <el-col :span="1">
                <div
                  style="margin: 5px auto;font-weight: bold;font-size: 22px"
                >{{formateNum(cindex+1)}}</div>
              </el-col>
              <el-col :span="11">
                <el-form-item
                  :prop="'Qslist.'+cindex+'.desc'"
                  style="margin: 0 15px"
                  label-width="0"
                  :rules="[
                  { required: true, message: '请输入选项值', trigger: 'blur' },
                  {min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: ['blur', 'change'] }
                ]"
                >
                  <el-input
                    class="self-fill-input"
                    placeholder="请输入问题描述"
                    v-model="Ques.desc"
                    style="width: 310px;"
                  ></el-input>
                </el-form-item>
              </el-col>
              <!--选择问题类型 -->
              <el-col :span="8" offset="4">
                <el-form-item prop="type" style="margin: 0 0px" label-width="0">
                  <el-select
                    v-model="Ques.type"
                    @change="changeType(cindex)"
                    style="width: 150px"
                    size="medium"
                  >
                    <el-option
                      v-for="item in types"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 卡片选项部分用于输入问题的回答选项-->
          <div class="options-part" style="text-align:left">
            <!-- 文本类型-->
            <el-input
              :disabled="true"
              v-if="Ques.type === 'input' || Ques.type === 'textarea'"
              :type="Ques.type"
              style="width: 560px"
              placeholder="待填写者输入"
            ></el-input>
            <!-- 单选/多选类型-->
            <div
              v-else-if="Ques.type === 'radio' || Ques.type ==='checkbox'"
              class="qs-text-radio-wrap"
            >
              <el-form-item label="最多可选" label-width="80px" v-if="Ques.type === 'checkbox'">
                <el-select
                  v-model="Qform.Qslist[cindex].options.ansNum"
                  style="width: 100px"
                  size="medium"
                >
                  <el-option
                    v-for="i in Qform.Qslist[cindex].options.values.length"
                    :key="i"
                    :label="i"
                    :value="i"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item
                v-for="(item, oindex) in Qform.Qslist[cindex].options.values"
                :label="'选项'+(oindex+1)"
                label-width="60px"
                :prop="'Qslist.'+cindex+'.options.values.'+oindex+'.label'"
                :rules="[
                  { required: true, message: '请输入选项值', trigger: 'blur' },
                  {max: 10, message: '长度在 1 到 30 个字符', trigger: ['blur', 'change'] }
                ]"
                :key="item.key"
              >
                <el-input
                  v-model="item.label"
                  placeholder="请输入选项"
                  class="self-fill-input"
                  style="width: 400px"
                  @focus="deleteVisibleSign = cindex*100+oindex"
                ></el-input>
                <i
                  class="el-icon-remove"
                  style="font-size: 15px;"
                  v-show="deleteVisibleSign == cindex*100+oindex"
                  @click="deleteOption(cindex, oindex)"
                ></i>
              </el-form-item>
            </div>
            <!-- 打分类型-->
            <el-rate disabled v-else-if="Ques.type == 'rate'"></el-rate>
          </div>
          <!-- 卡片底部用于增删选项-->
          <!-- 添加单选和多选选项的按钮-->
          <el-col :span="3" style="margin-top: 10px; margin-bottom: 10px">
            <el-button
              type="text"
              v-if="Ques.type === 'radio' || Ques.type === 'checkbox'"
              style="float: right;color: #0066FF;font-size: 12px"
              @click="addOption(cindex)"
            >添加选项</el-button>
          </el-col>
          <!-- 添加题目的级联操作-->
          <el-col :span="4" style="margin-top: 10px; margin-bottom: 10px">
            <el-button
              type="text"
              v-if="Ques.type === 'radio' || Ques.type === 'checkbox'"
              style="float: right;color: #0066FF;font-size: 12px"
              @click="dialogFormVisible = Ques.key"
            >添加关联题目</el-button>
          </el-col>
          <el-dialog :visible.sync="dialogFormVisible == Ques.key">
            <div slot="title" align="left">
              <span style="font-weight: bold">添加关联题目</span>
            </div>
            <el-form-item label="01.选择触发题目" style="font-weight: bold;" label-width="150px">
              <el-select
                v-model="Ques.pre.key"
                placeholder="请选择"
                @change="Ques.pre.type = findQuesWithKey(parseInt(Ques.pre.key)).type"
                size="medium"
                style="width: 270px"
              >
                <el-option
                  v-for="(item, qindex) in getPreviousQues(cindex)"
                  :key="item.key"
                  :label="'问题'+(qindex+1)+' '+item.desc"
                  :value="item.key.toString()"
                  :disabled="isValid(item)"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item
              label="输入触发值"
              v-if="Ques.pre.key != '' && (Ques.pre.type == 'text' || Ques.pre.type == 'textarea')"
              style="font-weight: bold;"
              label-width="150px"
            >
              <el-input v-model="Ques.pre.val" placeholder="请输入"></el-input>
            </el-form-item>
            <el-form-item
              label="选择触发选项"
              v-else-if="Ques.pre.key != '' && (Ques.pre.type == 'radio' || Ques.pre.type == 'checkbox')"
              style="font-weight: bold"
              label-width="150px"
            >
              <el-select multiple v-model="Ques.pre.options" placeholder="请选择" style="width: 270px">
                <el-option
                  v-for="(item, index) in findQuesWithKey(parseInt(Ques.pre.key)).options.values"
                  :key="item.key"
                  :label="'选项'+(index+1)+' '+item.label"
                  :value="item.key"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="02.选择跳转题目" style="font-weight: bold" label-width="150px">
              <el-select v-model="Ques.next" placeholder="请选择" size="medium" style="width: 270px">
                <el-option
                  v-for="(item,qindex) in getRearQues(cindex)"
                  :key="item.key"
                  :label="'问题'+(qindex+cindex+2)+' '+item.desc"
                  :value="item.key"
                  :disabled="isValid(item)"
                ></el-option>
              </el-select>
            </el-form-item>
            <div slot="footer" class="dialog-footer">
              <el-button @click="dialogFormVisible = -1" size="small">取 消</el-button>
              <el-button type="primary" @click="dialogFormVisible = -1" size="small">确 定</el-button>
            </div>
          </el-dialog>
          <el-col :span="6" offset="11" style="margin-top: 10px; margin-bottom: 10px">
            <!-- 设置问题必填与否的按钮-->
            <el-tooltip effect="dark" content="设为必填" placement="top">
              <el-button
                type="primary"
                v-show="Ques.required"
                icon="el-icon-star-off"
                size="mini"
                circle
                @click="Ques.required = !Ques.required"
              ></el-button>
            </el-tooltip>
            <el-tooltip effect="dark" content="取消必填" placement="top">
              <el-button
                type="primary"
                v-show="!Ques.required"
                icon="el-icon-star-on"
                size="mini"
                circle
                style="margin: 0 0"
                @click="Ques.required = !Ques.required"
              ></el-button>
            </el-tooltip>
            <!-- 添加问题或者拷贝问题的按钮-->
            <el-dropdown @command="handleCommand">
              <el-button type="info" icon="el-icon-document-copy" size="mini" circle></el-button>
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
                circle
                @click="deleteQues(cindex)"
              ></el-button>
            </el-tooltip>
          </el-col>
        </el-card>
      </div>
    </transition-group>
    <el-button id="release-btn" type="primary" @click="submitForm('Qform')">发布</el-button>
  </el-form>
</template>
<style>
@import url("//unpkg.com/element-ui@2.13.0/lib/theme-chalk/index.css");
</style>
<script>
const state = {
  baseSet: {
    radio: {
      values: [
        { label: "", key: 1 },
        { label: "", key: 2 }
      ],
      ansNum: 1
    },
    checkbox: {
      values: [
        { label: "", key: 3 },
        { label: "", key: 4 }
      ],
      ansNum: 1
    },
    input: null,
    textarea: null
  }
};
const Qs = {
  desc: "",
  type: "radio",
  required: false,
  options: state.baseSet["radio"],
  key: 0,
  pre: {
    key: "",
    options: [],
    type: "",
    val: ""
  },
  next: ""
};
export default {
  name: "Form",
  data() {
    return {
      Qform: {
        title: "",
        desc: "",
        Qslist: [JSON.parse(JSON.stringify(Qs))]
      },
      deleteVisibleSign: -1,
      isButtonVisible: true,
      key_counter: 5,
      dialogFormVisible: -1,
      types: [
        { label: "单选", value: "radio" },
        { label: "多选", value: "checkbox" },
        { label: "单行文本", value: "input" },
        { label: "多行文本", value: "textarea" },
        { label: "评分", value: "rate" }
      ],
      rules: {
        title: [
          { required: true, message: "请输入问卷名称", trigger: "blur" },
          { min: 3, max: 10, message: "长度在 3 到 30 个字符", trigger: "blur" }
        ],
        desc: [
          { required: true, message: "请输入问卷说明", trigger: "blur" },
          { min: 3, max: 30, message: "长度在 3 到 30 个字符", trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    addQues(cindex) {
      var qs = JSON.parse(JSON.stringify(Qs));
      (qs.key = this.key_counter),
        this.key_counter++,
        this.Qform.Qslist.splice(cindex + 1, 0, qs);
    },
    deleteQues(cindex) {
      if (this.Qform.Qslist.length == 1) return;
      this.isButtonVisible = false;
      this.Qform.Qslist.splice(cindex, 1);
      setTimeout(() => {
        this.isButtonVisible = true;
      }, 400);
    },
    copyQues(cindex) {
      var qs = JSON.parse(JSON.stringify(this.Qform.Qslist[cindex]));
      (qs.key = this.key_counter),
        this.key_counter++,
        this.Qform.Qslist.splice(cindex + 1, 0, qs);
    },
    changeType(cindex) {
      this.Qform.Qslist[cindex].options =
        state.baseSet[this.Qform.Qslist[cindex].type];
    },
    addOption(cindex) {
      switch (this.Qform.Qslist[cindex].type) {
        case "radio":
        case "checkbox":
          this.Qform.Qslist[cindex].options["values"].push({
            label: "",
            key: this.key_counter
          });
          this.key_counter++;
          break;
      }
    },
    deleteOption(cindex, oindex) {
      if (this.Qform.Qslist[cindex].options.values.length == 1) return;
      this.Qform.Qslist[cindex].options.values.splice(oindex, 1);
    },
    formateNum(num) {
      if (num < 10) return "0" + num;
      return num;
    },
    composeCommand(method, cindex) {
      return {
        method: method,
        index: cindex
      };
    },
    handleCommand(command) {
      if (command["method"] == "add") this.addQues(command["index"]);
      else this.copyQues(command["index"]);
    },
    getPreviousQues(cindex) {
      if (cindex == 0) return [];
      else return this.Qform.Qslist.slice(0, cindex);
    },
    getRearQues(cindex) {
      if (cindex == this.Qform.Qslist.length) return [];
      else return this.Qform.Qslist.slice(cindex + 1, this.Qform.Qslist.length);
    },
    findQuesWithKey(key) {
      for (var index in this.Qform.Qslist) {
        if (this.Qform.Qslist[index].key == key)
          return this.Qform.Qslist[index];
      }
    },
    isValid(Ques) {
      if (Ques.desc == "") return true;
      if (Ques.type == "radio" || Ques.type == "checkbox")
        for (var i in Ques.options.values)
          if (Ques.options.values[i].label == "") return true;
      return false;
    },
    submitForm(form) {
      this.$refs[form].validate(valid => {
        if (valid) {
          this.axios
            .post("/form", this.Qform)
            .then(successResponse => {
              if (successResponse.data === 200) {
                alert("问卷提交成功");
              } else {
                alert("问卷提交失败");
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
.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}

.box-card {
  width: 600px;
}

.el-input__icon .el-icon-remove:hover {
  visibility: "visible";
}
.self-fill-input /deep/ .el-input__inner {
  border-top-width: 0px;
  border-left-width: 0px;
  border-right-width: 0px;
  border-bottom-width: 1px;
  border-radius: 0;
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

#release-btn {
  position: absolute;
  right: 20%;
  bottom: 30%;
}
</style>