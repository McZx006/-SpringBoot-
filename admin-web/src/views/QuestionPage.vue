<template>
  <div>
    <h1>试题管理</h1>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="请输入试题关键词" />
      <el-select v-model="paperId" clearable placeholder="按试卷筛选">
        <el-option v-for="paper in papers" :key="paper.id" :label="paper.name" :value="paper.id" />
      </el-select>
      <el-button type="primary" @click="loadRows">搜索</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="primary" @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" border>
      <el-table-column prop="questionName" label="题目" min-width="220" />
      <el-table-column prop="paperName" label="所属试卷" width="160" />
      <el-table-column prop="questionType" label="题型" width="100" />
      <el-table-column prop="score" label="分值" width="80" />
      <el-table-column prop="answer" label="答案" width="120" />
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="680px">
      <el-form label-width="90px">
        <el-form-item label="所属试卷">
          <el-select v-model="form.paperId" placeholder="请选择试卷">
            <el-option v-for="paper in papers" :key="paper.id" :label="paper.name" :value="paper.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="form.questionType" @change="applyTypeTemplate">
            <el-option label="单选题" value="single" />
            <el-option label="多选题" value="multiple" />
            <el-option label="判断题" value="judge" />
            <el-option label="填空题" value="fill" />
          </el-select>
        </el-form-item>
        <el-form-item label="题目">
          <el-input v-model="form.questionName" type="textarea" />
        </el-form-item>
        <el-form-item label="选项JSON">
          <el-input v-model="form.optionsJson" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="答案">
          <el-input v-model="form.answer" placeholder="多选用英文逗号分隔，如 A,B,D" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" />
        </el-form-item>
        <el-form-item label="分值">
          <el-input-number v-model="form.score" :min="1" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>

      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '../api/request'

export default {
  data() {
    return {
      keyword: '',
      paperId: '',
      papers: [],
      rows: [],
      dialogVisible: false,
      editing: false,
      form: this.defaultForm()
    }
  },
  computed: {
    dialogTitle() {
      return this.editing ? '编辑试题' : '新增试题'
    }
  },
  async created() {
    await this.loadPapers()
    await this.loadRows()
  },
  methods: {
    async loadPapers() {
      const res = await request.get('/exampapers/page', { params: { page: 1, limit: 100 } })
      this.papers = res.data.data.list
    },
    async loadRows() {
      const res = await request.get('/examquestions/page', {
        params: { page: 1, limit: 20, keyword: this.keyword, paperId: this.paperId || undefined }
      })
      this.rows = res.data.data.list
    },
    reset() {
      this.keyword = ''
      this.paperId = ''
      this.loadRows()
    },
    openCreate() {
      this.editing = false
      this.form = this.defaultForm()
      this.dialogVisible = true
    },
    openEdit(row) {
      this.editing = true
      this.form = Object.assign(this.defaultForm(), row)
      this.dialogVisible = true
    },
    applyTypeTemplate() {
      if (this.form.questionType === 'judge') {
        this.form.optionsJson = '[{"key":"A","value":"对"},{"key":"B","value":"错"}]'
        this.form.answer = 'A'
      } else if (this.form.questionType === 'fill') {
        this.form.optionsJson = '[]'
      } else if (!this.form.optionsJson || this.form.optionsJson === '[]') {
        this.form.optionsJson = '[{"key":"A","value":""},{"key":"B","value":""},{"key":"C","value":""},{"key":"D","value":""}]'
      }
    },
    validate() {
      if (!this.form.paperId) return '请选择所属试卷'
      if (!this.form.questionName.trim()) return '请输入题目'
      if (!this.form.answer.trim()) return '请输入答案'
      if (!this.form.analysis.trim()) return '请输入题目解析'
      try {
        const options = JSON.parse(this.form.optionsJson || '[]')
        if ((this.form.questionType === 'single' || this.form.questionType === 'multiple') && (!Array.isArray(options) || options.length < 2)) {
          return '单选题和多选题至少需要两个选项'
        }
        if (this.form.questionType === 'judge' && (!Array.isArray(options) || options.length !== 2)) {
          return '判断题必须保留两个选项'
        }
      } catch (error) {
        return '选项JSON格式不正确'
      }
      return ''
    },
    async submit() {
      const error = this.validate()
      if (error) {
        this.$message.warning(error)
        return
      }
      if (this.editing) {
        await request.put('/examquestions/update', this.form)
      } else {
        await request.post('/examquestions/save', this.form)
      }
      this.dialogVisible = false
      await this.loadRows()
    },
    async remove(id) {
      await this.$confirm('确认删除这道试题吗？', '提示', { type: 'warning' })
      await request.delete('/examquestions/delete', { data: [id] })
      await this.loadRows()
    },
    defaultForm() {
      return {
        paperId: '',
        questionName: '',
        questionType: 'single',
        optionsJson: '[{"key":"A","value":""},{"key":"B","value":""},{"key":"C","value":""},{"key":"D","value":""}]',
        answer: 'A',
        analysis: '',
        score: 5,
        sort: 0
      }
    }
  }
}
</script>
