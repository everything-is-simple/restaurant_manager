<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'RuoYiGit',
  data() {
    return {
      message: '文瀛餐厅管理系统'
    }
  },
  methods: {
    goto() {
      this.$message.info(this.message)
    }
  }
}
</script>
