<template>
  <div>
    <h1>{{ title }}</h1>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="请输入关键词" />
      <el-button type="primary" @click="loadRows">搜索</el-button>
      <el-button @click="reset">重置</el-button>
      <el-button v-if="canCreate" type="primary" @click="openCreate">新增</el-button>
    </div>

    <el-table :data="rows" border>
      <el-table-column v-for="col in columns" :key="col.prop" :prop="col.prop" :label="col.label" />
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <el-button v-if="moduleName === 'messages'" size="mini" type="primary" @click="openReply(scope.row)">回复</el-button>
          <el-button v-if="canEdit" size="mini" @click="openEdit(scope.row)">编辑</el-button>
          <el-button v-if="canDelete" size="mini" type="danger" @click="remove(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form label-width="90px">
        <template v-if="moduleName === 'messages'">
          <el-form-item label="留言内容">
            <el-input v-model="form.content" type="textarea" disabled />
          </el-form-item>
          <el-form-item label="回复内容">
            <el-input v-model="form.reply" type="textarea" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'news'">
          <el-form-item label="标题">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="图片">
            <el-input v-model="form.picture" />
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="form.summary" type="textarea" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="form.content" type="textarea" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'banners'">
          <el-form-item label="名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="图片地址">
            <el-input v-model="form.value" />
          </el-form-item>
          <el-form-item label="说明">
            <el-input v-model="form.remark" />
          </el-form-item>
        </template>

        <template v-if="moduleName === 'forum'">
          <el-form-item label="标题">
            <el-input v-model="form.title" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="form.content" type="textarea" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
      dialogVisible: false,
      editing: false,
      form: {}
    }
  },
  computed: {
    moduleName() {
      if (this.$route.path.includes('messages')) return 'messages'
      if (this.$route.path.includes('banners')) return 'banners'
      if (this.$route.path.includes('forum')) return 'forum'
      if (this.$route.path.includes('storeup')) return 'storeup'
      if (this.$route.path.includes('examrecords')) return 'examrecords'
      return 'news'
    },
    title() {
      return {
        messages: '留言管理',
        news: '公告管理',
        banners: '轮播图管理',
        forum: '论坛管理',
        storeup: '收藏管理',
        examrecords: '考试记录'
      }[this.moduleName]
    },
    dialogTitle() {
      if (this.moduleName === 'messages') return '回复留言'
      return `${this.editing ? '编辑' : '新增'}${this.title}`
    },
    canCreate() {
      return this.moduleName === 'news' || this.moduleName === 'banners'
    },
    canEdit() {
      return this.canCreate || this.moduleName === 'forum'
    },
    canDelete() {
      return this.moduleName !== 'examrecords'
    },
    pageEndpoint() {
      return {
        messages: '/messages/page',
        news: '/news/page',
        banners: '/banners/page',
        forum: '/forum/page',
        storeup: '/storeup/page',
        examrecords: '/examrecords/page'
      }[this.moduleName]
    },
    columns() {
      return {
        messages: [
          { prop: 'username', label: '用户' },
          { prop: 'content', label: '留言内容' },
          { prop: 'reply', label: '回复' },
          { prop: 'statusText', label: '状态' }
        ],
        news: [
          { prop: 'title', label: '标题' },
          { prop: 'summary', label: '简介' },
          { prop: 'statusText', label: '状态' }
        ],
        banners: [
          { prop: 'name', label: '名称' },
          { prop: 'value', label: '图片地址' },
          { prop: 'remark', label: '说明' }
        ],
        forum: [
          { prop: 'title', label: '标题' },
          { prop: 'username', label: '作者' },
          { prop: 'statusText', label: '状态' },
          { prop: 'viewCount', label: '浏览' }
        ],
        storeup: [
          { prop: 'title', label: '收藏名称' },
          { prop: 'type', label: '收藏类型' },
          { prop: 'userId', label: '用户ID' },
          { prop: 'refId', label: '关联ID' }
        ],
        examrecords: [
          { prop: 'paperName', label: '试卷' },
          { prop: 'username', label: '用户' },
          { prop: 'score', label: '得分' },
          { prop: 'wrongCount', label: '错题数' }
        ]
      }[this.moduleName]
    }
  },
  watch: {
    '$route.path': {
      immediate: true,
      handler() {
        this.keyword = ''
        this.loadRows()
      }
    }
  },
  methods: {
    async loadRows() {
      const params = { page: 1, limit: 10, keyword: this.keyword }
      if (this.moduleName === 'news' || this.moduleName === 'forum') {
        params.publicOnly = false
      }
      const res = await request.get(this.pageEndpoint, { params })
      this.rows = res.data.data.list.map(item => ({
        ...item,
        statusText: item.status === 1 ? '已发布/已回复' : '待处理/隐藏'
      }))
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
    openReply(row) {
      this.editing = true
      this.form = Object.assign({ id: row.id, content: row.content, reply: row.reply || '' }, row)
      this.dialogVisible = true
    },
    async submit() {
      const error = this.validate()
      if (error) {
        this.$message.error(error)
        return
      }
      if (this.moduleName === 'messages') {
        await request.put('/messages/reply', { id: this.form.id, reply: this.form.reply })
      } else if (this.moduleName === 'news') {
        await request[this.editing ? 'put' : 'post'](this.editing ? '/news/update' : '/news/save', this.form)
      } else if (this.moduleName === 'banners') {
        await request[this.editing ? 'put' : 'post'](this.editing ? '/banners/update' : '/banners/save', this.form)
      } else if (this.moduleName === 'forum') {
        await request.put('/forum/update', this.form)
      }
      this.dialogVisible = false
      this.loadRows()
    },
    async remove(id) {
      await this.$confirm('确认删除这条数据吗？', '提示', { type: 'warning' })
      const endpoint = {
        messages: '/messages/delete',
        news: '/news/delete',
        banners: '/banners/delete',
        forum: '/forum/delete',
        storeup: '/storeup/delete'
      }[this.moduleName]
      await request.delete(endpoint, { data: [id] })
      this.loadRows()
    },
    validate() {
      if (this.moduleName === 'messages') {
        if (!this.form.reply || !this.form.reply.trim()) return '请输入回复内容'
      }
      if (this.moduleName === 'news') {
        if (!this.form.title || !this.form.title.trim()) return '请输入公告标题'
        if (!this.form.summary || !this.form.summary.trim()) return '请输入公告简介'
        if (!this.form.content || !this.form.content.trim()) return '请输入公告内容'
      }
      if (this.moduleName === 'banners') {
        if (!this.form.name || !this.form.name.trim()) return '请输入轮播图名称'
        if (!this.form.value || !this.form.value.trim()) return '请输入图片地址'
      }
      if (this.moduleName === 'forum') {
        if (!this.form.title || !this.form.title.trim()) return '请输入帖子标题'
        if (!this.form.content || !this.form.content.trim()) return '请输入帖子内容'
      }
      return ''
    },
    defaultForm() {
      if (this.moduleName === 'news') {
        return { title: '', picture: '', summary: '', content: '', status: 1 }
      }
      if (this.moduleName === 'banners') {
        return { name: 'banner', value: '', remark: '' }
      }
      if (this.moduleName === 'forum') {
        return { title: '', content: '', status: 1 }
      }
      return { content: '', reply: '' }
    }
  }
}
</script>
