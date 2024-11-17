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
import Particles, {initParticlesEngine} from "@tsparticles/react";
import {useEffect, useMemo, useState} from "react";
import {loadSlim} from "@tsparticles/slim";

/**
 * The background component of aristotle-ws.com's hompage.
 *
 * @param props  The regular React props object that contains only 1 attribute for now: "id", which is used for CSS
 * selector linking
 */
export default function ParticleBackground(props) {
  const [init, setInit] = useState(false);
  useEffect(() => {
    initParticlesEngine(async (engine) => {
      await loadSlim(engine);
    }).then(() => {
      setInit(true);
    });
  }, []);

  const particlesLoaded = (container) => {
    console.log(container);
  };


  const options = useMemo(
      () => ({
        background: {
          color: {
            value: "#1E2F97",
          },
        },
        fpsLimit: 120,
        interactivity: {
          events: {
            onClick: {
              enable: true,
              mode: "repulse",
            },
            onHover: {
              enable: true,
              mode: 'grab',
            },
          },
          modes: {
            push: {
              distance: 200,
              duration: 15,
            },
            grab: {
              distance: 150,
            },
          },
        },
        particles: {
          color: {
            value: "#FFFFFF",
          },
          links: {
            color: "#FFFFFF",
            distance: 150,
            enable: true,
            opacity: 0.3,
            width: 1,
          },
          move: {
            direction: "none",
            enable: true,
            outModes: {
              default: "bounce",
            },
            random: true,
            speed: 1,
            straight: false,
          },
          number: {
            density: {
              enable: true,
            },
            value: 150,
          },
          opacity: {
            value: 1.0,
          },
          shape: {
            type: "circle",
          },
          size: {
            value: {min: 1, max: 3},
          },
        },
        detectRetina: true,
      }),
      [],
  );

  return <Particles id={props.id} init={particlesLoaded} options={options}/>;
}
