/**
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';

import styles from './index.module.css';
import ParticleBackground from "@site/src/components/ParticleBackground";

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();

  return (
      <header className={styles.heroBanner}>
        <div className={styles.particlecanvas}>
          <ParticleBackground />
        </div>

        <div className="container">
          <h1 className="hero__title">
            <img src="img/logo.svg" alt="Aristotle Logo" width={200}/>
          </h1>
          <p className="hero__subtitle" style={{ color: "white" }}>
            Webservice with first-class support for <b><em>Graph Database</em></b>
          </p>
          <div className={styles.buttons}>
            <Link
                className="button button--secondary button--lg"
                to="/docs/intro"
            >
              Get Started
            </Link>
            <div
                className="button button--secondary button--lg"
                style={{marginLeft: '10px'}}
            >
              <a
                  href="https://github.com/paion-data/aristotle"
                  rel="noopener"
                  target="_blank"
                  aria-label="Star paion-data/aristotle on GitHub"
                  style={{
                    textDecoration: 'none',
                    color: 'inherit',
                    display: 'flex',
                    alignItems: 'center',
                  }}
              >
                <svg
                    viewBox="0 0 16 16"
                    width="16"
                    height="16"
                    className="octicon octicon-mark-github"
                    aria-hidden="true"
                >
                  <path
                      fillRule="evenodd"
                      d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"
                  ></path>
                </svg>
                &nbsp;<span>Star</span>
              </a>
            </div>
          </div>
        </div>
      </header>
  );
}

export default function Home(): JSX.Element {
  const {siteConfig} = useDocusaurusContext();
  return (
      <Layout
          title={`${siteConfig.title}`}
          description="Description will go into a meta tag in <head />">
        <HomepageHeader/>
        <main>
          <HomepageFeatures/>
        </main>
      </Layout>
  );
}
