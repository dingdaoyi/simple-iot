# Security Policy

## Supported versions

The latest minor release on the `main` branch always receives security updates. Older versions receive fixes only when explicitly back-ported.

| Version | Supported          |
| ------- | ------------------ |
| latest `main` | ✅ |
| < 0.1   | ❌ |

## Reporting a vulnerability

**Please do not open a public GitHub issue for security vulnerabilities.**

Instead, report privately:

1. Use GitHub's [private vulnerability reporting](https://github.com/dingdaoyi/simple-iot/security/advisories/new) (preferred).
2. Or email the maintainer at the address listed on the GitHub profile [@dingdaoyi](https://github.com/dingdaoyi).

Please include:

- A description of the vulnerability and its impact
- Steps to reproduce (proof-of-concept welcome)
- Affected version / commit SHA
- Your suggested fix, if any

## What to expect

| Stage | Target time |
|-------|-------------|
| Acknowledgement of report | ≤ 72 hours |
| Initial assessment | ≤ 7 days |
| Patch & coordinated disclosure | ≤ 30 days for critical issues |

We will keep you informed throughout the process and credit you in the release notes (unless you prefer to remain anonymous).

## Scope

In scope:
- Authentication / authorisation bypass
- Remote code execution
- SQL / NoSQL injection
- Sensitive data exposure
- Server-side request forgery (SSRF)
- Cross-site scripting (XSS) in the bundled web UI
- Vulnerabilities in the default Docker images we ship

Out of scope (please don't report):
- Vulnerabilities in third-party services or integrations not maintained here
- Self-inflicted issues caused by deploying with default credentials in production
- Lack of rate-limiting on auxiliary endpoints
- Reports generated solely by automated scanners with no PoC

Thank you for helping keep Simple IoT and its users safe.
