
import http from 'k6/http';
import { check } from 'k6';

const PORT = __ENV.PORT || '8081';

export const options = {
  scenarios: {
    io_load: {
      executor: 'constant-vus',
      vus: 500,
      duration: '30s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const response = http.get(
    `http://localhost:${PORT}/api/benchmark/io?delayMs=500`
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}
