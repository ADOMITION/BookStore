<template>
  <div class="password-strength">
    <div
        class="strength-bar"
        :class="'level-' + strengthLevel"
        :style="{ width: strengthWidth }"
    ></div>
    <span class="strength-text">{{ strengthText }}</span>
  </div>
</template>

<script>

export default {
  props: ['password'],
  computed: {
    strengthLevel() {
      if (!this.password) return 0;

      let score = 0;

      // 长度评分（最高4分）
      const length = this.password.length;
      score += Math.min(Math.floor(length / 3), 4); // 每3字符加1分，最高4分

      // 字符类型评分（最高4分）
      const hasLower = /[a-z]/.test(this.password);
      const hasUpper = /[A-Z]/.test(this.password);
      const hasNumber = /\d/.test(this.password);
      const hasSpecial = /[^A-Za-z\d]/.test(this.password);

      // 统计有效字符类型数量
      const typeCount = [hasLower, hasUpper, hasNumber, hasSpecial].filter(Boolean).length;
      score += typeCount * 2; // 每种类型加2分

      // 同时包含大小写和特殊字符
      if (hasLower && hasUpper && hasSpecial) score += 3;

      // 复杂度扣分项
      score -= this.calculateDeductions();

      // 将总分映射到0-5级（总分除以3取整（因为我们最高就是按照每三分一个等级floor向下取整)，
      // 使用 Math.max() 是为了确保结果不会小于 0）
      // 使用 Math.min() 是为了确保结果不会大于 5
      return Math.min(Math.max(Math.floor(score / 3), 0), 5); // 0-5级
    },
    strengthWidth() {
      const widths = ['15%', '30%', '50%', '65%', '85%', '100%'];
      return widths[this.strengthLevel] || '0%';
    },
    strengthText() {
      const texts = ['极弱', '非常弱', '弱', '中等', '强', '非常强'];
      return texts[this.strengthLevel];
    }
  },
  methods: {
    calculateDeductions() {
      let deductions = 0;
      const pass = this.password;

      // 连续字符扣分（如1234、abcd）
      // 要检查连续的 4 个字符，所以 i 最大只能到 pass.length - 4，这样才能保证 i + 3 不会超出字符串的长度范围。
      for (let i = 0; i < pass.length - 3; i++) {
        const s1 = pass.charCodeAt(i);
        const s2 = pass.charCodeAt(i + 1);
        const s3 = pass.charCodeAt(i + 2);
        const s4 = pass.charCodeAt(i + 3);

        // 检查是否连续递增或递减（数字或字母）
        if (
            (s2 === s1 + 1 && s3 === s2 + 1 && s4 === s3 + 1) ||
            (s2 === s1 - 1 && s3 === s2 - 1 && s4 === s3 - 1)
        ) {
          deductions += 3;
        }
      }

      // 重复字符扣分（如aaaa）
      if (/(.)\1{3,}/.test(pass)) deductions += 2;

      // 全数字或全字母扣分
      //+前面的元素至少出现一次
      //^表示匹配字符串的开始位置。
      //$匹配字符串的结束位置
      if (/^\d+$/.test(pass) || /^[A-Za-z]+$/.test(pass)) deductions += 2;

      return Math.min(deductions, 8); // 最多扣8分
    }
  }
}
</script>

<style scoped>
.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
}

.strength-bar {
  height: 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.strength-bar[class*="level-0"] {
  background: #ff4d4f;
  width: 15%;
}

.strength-bar[class*="level-1"] {
  background: #ff7d45;
  width: 30%;
}

.strength-bar[class*="level-2"] {
  background: #ffa940;
  width: 50%;
}

.strength-bar[class*="level-3"] {
  background: #ffc53d;
  width: 65%;
}

.strength-bar[class*="level-4"] {
  background: #73d13d;
  width: 85%;
}

.strength-bar[class*="level-5"] {
  background: #389e0d;
  width: 100%;
}

.strength-text {
  margin-left: 8px;
  font-size: 12px;
  color: #666;
  min-width: 50px;
}
</style>