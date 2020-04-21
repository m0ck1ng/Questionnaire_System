<template>
  <div>
    <NavMenu></NavMenu>
    <div>
      <router-view/>
    </div>
  </div>
</template>

<style scoped>
div {
  height: 100%;
}
</style>

<script>
import NavMenu from '@/components/NavMenu'
export default {
  name: "Index",
  components: {
    NavMenu
  },
  data() {

  },
  methods: {

  }
};
</script>