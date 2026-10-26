import http from 'k6/http';
import { check } from 'k6';

// Environment variables
const port = __ENV.PORT || '8081';
const users = Number(__ENV.USERS || 100);
const delayMs = Number(__ENV.DELAY_MS || 500);
const duration = __ENV.DURATION || '60s';

// Print configuration before running
console.log('======================================');
console.log(' PostgreSQL JDBC Benchmark');
console.log('======================================');
console.log(`Port: ${port}`);
console.log(`Concurrent Users: ${users}`);
console.log(`Database Delay: ${delayMs} ms`);
console.log(`Duration: ${duration}`);
console.log('======================================');

export const options = {
  scenarios: {
    database_load: {
      executor: 'constant-vus',
      vus: users,
      duration: duration,
      gracefulStop: '30s',
    },
  },

  thresholds: {
    // Less than 1% failed requests
    http_req_failed: ['rate<0.01'],

    // Generous latency threshold for connection-pool contention
    http_req_duration: ['p(95)<15000'],
  },
};

export default function () {
  const url =
    `http://127.0.0.1:${port}/api/benchmark/database-load?delayMs=${delayMs}`;

  const response = http.get(url, {
    timeout: '30s',
    tags: {
      workload: 'postgresql',
      thread_mode: port === '8081' ? 'virtual' : 'platform',
    },
  });

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}