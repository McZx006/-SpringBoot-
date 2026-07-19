<template>
  <div>
    <h1>{{ title }}</h1>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="请输入关键词" />
      <el-button type="primary" @click="loadRows">搜索</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button type="primary" @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" border>
      <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" />
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="mini" @click="openEdit(scope.row.raw)">编辑</el-button>
          <el-button size="mini" type="danger" @click="remove(scope.row.raw.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="560px">
      <el-form label-width="90px">
        <template v-if="moduleName === 'student'">
          <el-form-item label="学号">
            <el-input v-model="form.xuehao" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" placeholder="请选择">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'resource'">
          <el-form-item label="资料名称">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="资料类型">
            <el-select v-model="form.typeId" placeholder="请选择">
              <el-option v-for="item in resourceTypes" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="form.summary" type="textarea" />
          </el-form-item>
          <el-form-item label="资料文件">
            <el-upload
              action="/api/file/upload"
              :headers="uploadHeaders"
              :data="{ type: 'file' }"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
            >
              <el-button type="primary">上传文件</el-button>
            </el-upload>
            <el-input v-model="form.fileUrl" placeholder="上传后自动填入，也可手动输入文件地址" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'paper'">
          <el-form-item label="试卷名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="说明">
            <el-input v-model="form.description" type="textarea" />
          </el-form-item>
          <el-form-item label="考试时长">
            <el-input-number v-model="form.duration" :min="1" />
          </el-form-item>
          <el-form-item label="总分">
            <el-input-number v-model="form.totalScore" :min="1" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'resourceType'">
          <el-form-item label="类型名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="form.sort" :min="0" />
          </el-form-item>
        </template>
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
      rows: [],
      resourceTypes: [],
      dialogVisible: false,
      form: {},
      editing: false
    }
  },
  computed: {
    moduleName() {
      const map = {
        '/admin/xueyuan': 'student',
        '/admin/resource-types': 'resourceType',
        '/admin/resources': 'resource',
        '/admin/exampapers': 'paper'
      }
      return map[this.$route.path] || 'student'
    },
    title() {
      const map = {
        student: '学员管理',
        resourceType: '资料类型管理',
        resource: '学习资料管理',
        paper: '试卷管理'
      }
      return map[this.moduleName]
    },
    dialogTitle() {
      return `${this.editing ? '编辑' : '新增'}${this.title}`
    },
    columns() {
      return {
        student: [
          { prop: 'xuehao', label: '学号' },
          { prop: 'name', label: '姓名' },
          { prop: 'phone', label: '手机号' },
          { prop: 'statusText', label: '状态' },
          { prop: 'addtime', label: '创建时间' }
        ],
        resourceType: [
          { prop: 'name', label: '类型名称' },
          { prop: 'sort', label: '排序' },
          { prop: 'addtime', label: '创建时间' }
        ],
        resource: [
          { prop: 'title', label: '资料名称' },
          { prop: 'typeId', label: '类型ID' },
          { prop: 'statusText', label: '状态' },
          { prop: 'addtime', label: '创建时间' }
        ],
        paper: [
          { prop: 'name', label: '试卷名称' },
          { prop: 'duration', label: '时长(分钟)' },
          { prop: 'totalScore', label: '总分' },
          { prop: 'statusText', label: '状态' },
          { prop: 'addtime', label: '创建时间' }
        ]
      }[this.moduleName]
    },
    uploadHeaders() {
      return { Token: localStorage.getItem('adminToken') || '' }
    },
    pageEndpoint() {
      return {
        student: '/xueyuan/page',
        resourceType: '/resource-types/page',
        resource: '/resources/page',
        paper: '/exampapers/page'
      }[this.moduleName]
    },
    saveEndpoint() {
      return {
        student: '/xueyuan/save',
        resourceType: '/resource-types/save',
        resource: '/resources/save',
        paper: '/exampapers/save'
      }[this.moduleName]
    },
    updateEndpoint() {
      return {
        student: '/xueyuan/update',
        resourceType: '/resource-types/update',
        resource: '/resources/update',
        paper: '/exampapers/update'
      }[this.moduleName]
    },
    deleteEndpoint() {
      return {
        student: '/xueyuan/delete',
        resourceType: '/resource-types/delete',
        resource: '/resources/delete',
        paper: '/exampapers/delete'
      }[this.moduleName]
    }
  },
  watch: {
    '$route.path': {
      immediate: true,
      handler() {
        this.keyword = ''
        this.loadRows()
        this.loadTypes()
      }
    }
  },
  methods: {
    async loadRows() {
      const params = { page: 1, limit: 10 }
      if (this.moduleName === 'student') {
        params.name = this.keyword
      } else {
        params.keyword = this.keyword
      }
      if (this.moduleName === 'resource') {
        params.publicOnly = false
      }
      if (this.moduleName === 'paper') {
        params.publicOnly = false
      }
      const res = await request.get(this.pageEndpoint, { params })
      this.rows = res.data.data.list.map(item => ({
        ...item,
        id: item.id,
        name: item.name || item.title || item.xuehao || item.username || '未命名',
        statusText: item.status === 0 ? '禁用/下架' : '正常',
        addtime: item.addtime || '',
        raw: item
      }))
    },
    async loadTypes() {
      if (this.moduleName !== 'resource') return
      const res = await request.get('/resource-types/list')
      this.resourceTypes = res.data.data
    },
    reset() {
      this.keyword = ''
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
    handleUploadSuccess(res) {
      if (res.code === 0) {
        this.form.fileUrl = res.data.url
        this.$message.success('上传成功')
      } else {
        this.$message.error(res.msg || '上传失败')
      }
    },
    async submit() {
      const error = this.validate()
      if (error) {
        this.$message.error(error)
        return
      }
      if (this.editing) {
        await request.put(this.updateEndpoint, this.form)
      } else {
        await request.post(this.saveEndpoint, this.form)
      }
      this.dialogVisible = false
      this.loadRows()
    },
    async remove(id) {
      await this.$confirm('确认删除这条数据吗？', '提示', { type: 'warning' })
      await request.delete(this.deleteEndpoint, { data: [id] })
      this.loadRows()
    },
    validate() {
      if (this.moduleName === 'student') {
        if (!this.form.xuehao || !this.form.xuehao.trim()) return '请输入学号'
        if (!this.form.name || !this.form.name.trim()) return '请输入姓名'
        if (this.form.phone && !/^1\d{10}$/.test(this.form.phone)) return '手机号格式不正确'
        if (this.form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) return '邮箱格式不正确'
      }
      if (this.moduleName === 'resourceType') {
        if (!this.form.name || !this.form.name.trim()) return '请输入资料类型名称'
      }
      if (this.moduleName === 'resource') {
        if (!this.form.title || !this.form.title.trim()) return '请输入资料名称'
        if (!this.form.typeId) return '请选择资料类型'
        if (!this.form.summary || !this.form.summary.trim()) return '请输入资料简介'
        if (!this.form.fileUrl || !this.form.fileUrl.trim()) return '请上传资料文件或填写文件地址'
      }
      if (this.moduleName === 'paper') {
        if (!this.form.name || !this.form.name.trim()) return '请输入试卷名称'
        if (!this.form.description || !this.form.description.trim()) return '请输入试卷说明'
        if (!this.form.duration || this.form.duration <= 0) return '考试时长必须大于0'
        if (!this.form.totalScore || this.form.totalScore <= 0) return '总分必须大于0'
      }
      return ''
    },
    defaultForm() {
      if (this.moduleName === 'student') {
        return { xuehao: '', name: '', gender: '男', phone: '', email: '' }
      }
      if (this.moduleName === 'resourceType') {
        return { name: '', sort: 0 }
      }
      if (this.moduleName === 'resource') {
        return { title: '', typeId: '', summary: '', fileUrl: '', status: 1, author: '管理员' }
      }
      return { name: '', description: '', duration: 60, totalScore: 100, status: 1 }
    }
  }
}
</script>
