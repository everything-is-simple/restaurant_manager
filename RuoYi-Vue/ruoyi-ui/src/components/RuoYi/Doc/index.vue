<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'RuoYiDoc',
  data() {
    return {
      message: '项目文档见 docs/plan_ruoyi_classic'
    }
  },
  methods: {
    goto() {
      this.$message.info(this.message)
    }
  }
}
</script>
