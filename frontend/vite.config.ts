import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  // During development the Spring Boot API runs on port 8080.
  server: { proxy: { '/api': 'http://localhost:8080', '/actuator': 'http://localhost:8080' } },
  test: { environment: 'jsdom', globals: true, setupFiles: ['./src/test-setup.ts'] },
})
