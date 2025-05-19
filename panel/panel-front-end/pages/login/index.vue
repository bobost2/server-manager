<script setup lang="ts">
definePageMeta({
  layout: false,
  middleware: 'login'
})

useHead({
  title: 'Server Manager - Login'
})

const showPass = ref(false)
const username = ref('')
const password = ref('')

function createCookie() {
  const test = useCookie("auth", {
    maxAge: 60 * 60
  });
  test.value = "SampleAuthMess";

  navigateTo("/");
}

async function onSubmit() {
  //alert(username.value + " " + password.value);

  await $fetch('http://localhost:8080/user/login', {
    method: 'POST',
    body: {
      username: username.value,
      password: password.value
    },
    credentials: 'include'
  }).catch(error => {
    console.log(error.statusCode)
  })

  await $fetch("http://localhost:8080/user/info", {
    credentials: 'include'
  }).then((res) => {
    console.log(res)
  })
}

</script>

<template>
  <div class="center-page">
    <div class="form-container">
      <h1>Login</h1>
      <v-form validate-on="submit lazy" @submit.prevent="onSubmit">
        <v-text-field label="Username" variant="outlined" prepend-inner-icon="mdi-account-circle" v-model="username">
        </v-text-field>
        <v-text-field label="Password" variant="outlined" prepend-inner-icon="mdi-form-textbox-password"
                      :append-inner-icon="showPass ? 'mdi-eye' : 'mdi-eye-off'" :type="showPass ? 'text' : 'password'"
                      @click:append-inner="showPass = !showPass" v-model="password"></v-text-field>
        <div class="btn-container">
          <v-checkbox label="Stay logged in" style="display: inline-flex"></v-checkbox>
          <v-btn variant="tonal" type="submit">
            Login
          </v-btn>
        </div>
      </v-form>
    </div>
  </div>
</template>

<style scoped>
  .center-page {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
  }

  .form-container {
    border: 2px solid var(--border-color);
    border-radius: 5px;
    padding: 20px;
    width: 550px;
    box-shadow: 0 0 12px 0 #0000004f;
  }

  h1 {
    margin-bottom: 20px;
  }

  .btn-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    margin-top: -10px;
  }
</style>