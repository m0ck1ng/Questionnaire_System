<template>
  <div class="app-container">
    <h2 :style="{ marginLeft: margin }">{{ title }}&nbsp填报人数 {{ tableData.length }}</h2>
    <div v-if="loaded">
      <el-button type="primary" class="download-btn" :style="{ marginLeft: margin }" @click="download()">导出问卷结果</el-button>
      <el-collapse v-model="activeNames" :style="{ marginLeft: margin, marginRight: margin }">
        <el-collapse-item
          v-for="(desc, index) in descriptions"
          :key="index"
          :name="index"
          class="collapse-item"
        >
          <div slot="title">
            <span style="font-weight: bold">{{ '问题'+(index+1)+' '+desc+' ('+labels[types[index]]+')' }}</span>
          </div>
          <div v-if="types[index] == 'radio' || types[index] == 'rate' || types[index] == 'checkbox'" :id="'ques'+index" style="height: 150px; width:300px" />
          <el-table
            :data="tableData"
            style="width: 100%"
            height="200"
            :show-summary="types[index] == 'num'"
          >
            <el-table-column
              :prop="''+(types.length)"
              label="时间"
              class="table-col"
            />
            <el-table-column
              :prop="''+index"
              label="内容"
              class="table-col"
            />
            <el-table-column
              :prop="''+(types.length+1)"
              label="IP地址"
              class="table-col"
            />
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div v-if="noDataSignal">
      <h3 :style="{ marginLeft: margin }">该问卷暂无数据</h3>
    </div>
  </div>
</template>
<script>
import { getToken } from '../../utils/auth'
import { getReplyStatis, downloadReplys } from '@/api/reply'
import fileDownload from 'js-file-download'
import echarts from '@/echarts'
export default {
  data() {
    return {
      activeNames: [],
      title: '',
      descriptions: '',
      statis: '',
      types: '',
      tableData: [],
      loaded: false,
      noDataSignal: false,
      labels: { 'radio': '单选',
        'checkbox': '多选',
        'input': '单行文本',
        'textarea': '多行文本',
        'rate': '评分',
        'num': '数字',
        'pos': '地理位置'
      }
    }
  },
  computed: {
    margin: function() {
      var margin = 5
      if (document.body.clientWidth > 600) { margin = 20 }
      return margin + 'px'
    }
  },
  created: function() {
    var token = getToken()
    var id = this.$route.params.id
    getReplyStatis(token, id).then(response => {
      this.title = response.title
      this.descriptions = response.desc
      this.statis = response.statis
      this.types = response.types
      for (var i in response.datatable) {
        var item = {}
        for (var j in response.datatable[i]) {
          item[String(j)] = response.datatable[i][j]
        }
        this.tableData.push(item)
      }
      if (this.tableData.length === 0) {
        this.noDataSignal = true
      } else {
        this.loaded = true
      }
    })
  },
  mounted: function() {
    this.$nextTick(() => {
      setTimeout(() => {
        this.drawChart()
      }, 500)
    })
  },
  methods: {
    drawChart() {
      var chartList = []
      for (var i in this.statis) {
        var index = parseInt(i)
        if (this.types[index] === 'radio' || this.types[index] === 'rate' || this.types[index] === 'checkbox') {
          var chart = echarts.init(document.getElementById('ques' + i))
          var data = []
          for (var key in this.statis[i]) {
            var name = key
            if (this.types[index] === 'rate') { name = name + '分' }
            data.push({ 'name': name, 'value': this.statis[i][key] })
          }
          var option = {
            series: {
              type: 'pie',
              data: data
            },
            tooltip: {
              formatter: '{b} <br/>{c}个 ({d}%)'
            }
          }
          chart.setOption(option)
          chartList.push(chart)
        }
      }
    },
    download() {
      var token = getToken()
      var id = this.$route.params.id
      downloadReplys(token, id).then(response => {
        fileDownload(response.data, this.title + '.xls')
      })
    }
  }
}
</script>
<style>
.download-btn {
  margin-bottom: 15px;
}
.table-row {
  width: 10%;
  min-width: 100px;
}
</style>
