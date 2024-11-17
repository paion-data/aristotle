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
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Open API',
    Svg: require('@site/static/img/openapi-logo.svg').default,
    scale: 0.7,
    id: 'not-used',
    description: (
        <>
          The API documentation is powered by <a href="https://swagger.io/">Swagger</a>, providing comprehensive and
          interactive API documentation.
        </>
    ),
  },
  {
    title: 'Graph Data',
    Svg: require('@site/static/img/graph.svg').default,
    scale: 1,
    id: 'graph-icon',
    description: (
        <>
          Designed for Knowledge Graph and connection inference, Aristotle has first-class support for Neo4J database as
          a back-end and flexible pipeline-style architecture which handles nearly any graph database for data storage
        </>
    ),
  },

  {
    title: 'Open Source',
    Svg: require('@site/static/img/apache.svg').default,
    scale: 0.7,
    id: 'not-used',
    description: (
        <>
          Astraios is 100% open source and available on <a href="https://github.com/paion-data/aristotle">Github</a>.
          Released
          under the commercial-friendly
          <a href="http://www.apache.org/licenses/LICENSE-2.0.html"> Apache License, Version 2.0.</a>
        </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
      <div className={clsx('col col--4')} style={{color: "white"}}>
        <div className="text--center">
          <Svg className={styles.featureSvg} role="img"/>
        </div>
        <div className="text--center padding-horiz--md">
          <Heading as="h3">{title}</Heading>
          <p>{description}</p>
        </div>
      </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
      <section className={styles.features}>
        <div className="container">
          <div className="row">
            {FeatureList.map((props, idx) => (
                <Feature key={idx} {...props} />
            ))}
          </div>
        </div>
      </section>
  );
}
