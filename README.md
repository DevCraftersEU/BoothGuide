# 💁 BoothGuide

Booth guide is a simple tool to manage and display exhibitors at exhibitions and similar events.
The app (Frontend) is built with [vue.js](https://vuejs.org/) + [vuetify](https://vuetifyjs.com/) and works as an SPA /
PWA.
This means that the app can be installed on any device you like.
The data is stored in a [Spring Boot](https://spring.io/projects/spring-boot) application (Backend) using a PostgreSQL
backend.

Once loaded, the application stores all data on the device and works completely offline.
This allows users to view all exhibitor information even in places where no internet is available.
Data is updated when the application is opened again or when manually requested.

## 🏁 Features and Roadmap

- [x] 🙋 Displaying exhibitor information (name, location, offerings)
- [x] ⭐ Save and show favorites (also offline)
- [x] 🔍 Searching for exhibitors or exhibitors' information
- [x] ✅ Filter by offerings
- [x] 🔳 Light and dark and own designs
- [x] 📱 Installable as PWA
- [x] 🔁 Import and export exhibitor data
- [x] ➖ Customizable footer (sponsors, messages, images, ...)
- [x] 🙆‍♀️ Multiple user management with roles (Designer, Moderator, Admin)


- [ ] 💬 Display Specific texts for each exhibitor
- [ ] 🌎 i18n - currently only english and german
- [ ] 🔐 Expansion of authentication options (OAuth2 / OIDC)

## 💌 Legal Information

The project may be used for both private and business purposes. However, selling the project or any derivative works is
strictly prohibited. Additionally, offering this project or any modified version as a Software-as-a-Service (SaaS) is
not allowed. Modifications and further developments are permitted as long as they comply with these restrictions. For
more details, please refer to the LICENSE file.

## 🎯 Deployment

To get this project up and running, [docker](https://www.docker.com/)
and [docker compose](https://docs.docker.com/compose/) must be installed.

### 🚀 Setup Steps with `docker-compose.yml`

#### Use prebuild docker images

1. Download [`docker-compose.yml`](./docker-compose.yml) from this repository
2. Create an `.env` file in the same directory and copy the content from [`env.template`](./env.template)
3. Modify the values in `.env` and provide all values
4. Run `docker compose up -d`
5. Open `http://localhost:7000` in the browser

#### Alternative: Build docker images

1. Clone this repository: `git clone`
2. Copy `env.template` to `.env`
3. Modify `.env` and provide all values
4. Run `docker compose -f docker-compose-build.yml build`
5. Run `docker compose -f docker-compose-build.yml up -d`
6. Open `http://localhost:7000` in the browser

### ⚙️ Environment Variables

#### Application Properties

| Variable               | Required | Description                                                                         |
|------------------------|----------|-------------------------------------------------------------------------------------|
| `APPLICATION_NAME`     | ✅        | The name of the application, displayed in various locations.                        |
| `APPLICATION_SUBTITLE` | ✅        | Subtitle of the application, displayed on start page below the title.               |
| `PWA_TITLE`            | ✅        | The title of the application as displayed when installing the Progressive Web App.  |
| `PWA_TITLE_SHORT`      | ✅        | A shorter version of the application title, used if the original title is too long. |
| `DATASOURCE_URL`       | ✅        | The URL of the data source (e.g., PostgreSQL database) used by the application.     |
| `DATASOURCE_USERNAME`  | ✅        | The username for accessing the database.                                            |
| `DATASOURCE_PASSWORD`  | ✅        | The password for accessing the database.                                            |

#### Configuration Properties

| Variable            | Default    | Required | Description                                                                                                                                                                                                                                                                |
|---------------------|------------|----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ADMIN_USERNAME`    | `admin`    | ✖️       | The username for the admin account. Only needed at first boot.<br><br>**Important:** Changing this variable after the first start of the application has no effect!                                                                                                        |
| `ADMIN_PASSWORD`    | `<random>` | ✖️       | The password for the admin account. Only needed at first boot. If this variable does not exist at first boot, a random password is generated and printed in the logs.<br><br>**Important:** Changing this variable after the first start of the application has no effect! |
| `ENABLE_API_DOCS`   | `false`    | ✖️       | Enables the [OpenAPI documentation](https://swagger.io/specification/) to facilitate adding data from other clients (e.g., bulk imports with python). Disabled by default.                                                                                                 |
| `ENABLE_SWAGGER_UI` | `false`    | ✖️       | Enables the [Swagger UI](https://swagger.io/tools/swagger-ui/) to facilitate API interaction. Disabled by default. If it is enabled, OpenAPI documentation must also be enabled because the Swagger UI uses the Open API docs.                                             |
| `SECURE_SPRINGDOC`  | `true`     | ✖️       | Controls access to the OpenAPI documentation and the Swagger UI. When set to `true`, only admin users can access those endpoints. If set to false, everybody can see the OpenAPI specification and the Swagger UI. Authentication is still needed to add or modify data.   |

#### Design Properties

| Variable                    | Required | Default | Description                                                                                                                                                                                                                                                                                    |
|-----------------------------|----------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DEFAULT_DESIGN_ENABLE`     | ✖️       | `false` | Set to `true` if you want to provide a default layout. This is only needed if you want to provide a predefined default theme. Otherwise, only dark and light designs will be provided on first start and a default layout can be set in the admin interface after the application has started. |
| `DEFAULT_DESIGN_BACKGROUND` | ✖️       | `---`   | **Must be set if default design is enabled!** Predefined background color for the default layout if the application is deployed with a default theme.                                                                                                                                          |
| `DEFAULT_DESIGN_SURFACE`    | ✖️       | `---`   | **Must be set if default design is enabled!** Predefined surface color for the application surface if the application is deployed with a default theme.                                                                                                                                        |
| `DEFAULT_DESIGN_PRIMARY`    | ✖️       | `---`   | **Must be set if default design is enabled!** Predefined primary color for the application if the application is deployed with a default theme.                                                                                                                                                |

#### Footer Properties

| Variable           | Required | Default | Description                                                                                                                               |
|--------------------|----------|---------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `SHOW_FOOTER`      | ✖️       | `false` | Set to `true` if you want to enable the footer to be displayed on the front end.                                                          |
| `FOOTER_MESSAGE`   | ✖️       | `---`   | The message to be displayed in the footer. If not specified, only the image will be displayed.                                            |
| `FOOTER_IMAGE_XS`  | ✖️       | `---`   | The image displayed on screens with a [width of XS](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds).  |
| `FOOTER_IMAGE_SM`  | ✖️       | `---`   | The image displayed on screens with a [width of SM](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds).  |
| `FOOTER_IMAGE_MD`  | ✖️       | `---`   | The image displayed on screens with a [width of MD](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds).  |
| `FOOTER_IMAGE_LG`  | ✖️       | `---`   | The image displayed on screens with a [width of LG](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds).  |
| `FOOTER_IMAGE_XL`  | ✖️       | `---`   | The image displayed on screens with a [width of XL](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds).  |
| `FOOTER_IMAGE_XXL` | ✖️       | `---`   | The image displayed on screens with a [width of XXL](https://vuetifyjs.com/en/features/display-and-platform/#breakpoints-and-thresholds). |

**Important**: If you use an image, make sure you provide at least the XS image. When the frontend requests an image, it
also sends the size of the display. The backend will then try to find the best matching image (e.g. if the screen size
is XL, the backend will test if there is an image for an XL screen. If not, it will try to get an image for an LG
screen, and so on). So by providing an image for XS screens, you will always have a fallback option.

## 🌐 Secure Communication

To enable secure TLS connections, a reverse proxy such as [nginx](https://nginx.org/) is recommended. The following
nginx server configuration can be used for this:

```nginx
server {
    server_name _;

    location / {
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_pass http://localhost:7000;
    }

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    listen 443 ssl; 
    ssl_certificate <Path to Certificate>;
    ssl_certificate_key <Path to Key>; 
    ssl_dhparam <Path to DHParams>;

}
server {
    server_name _;
    listen 80;
    return 301 https://$host$request_uri;

}
```

## 🔨 Contribution Instructions

To contribute to this project you have to check out the repository first. To get the project running locally, follow
the steps below.

### 🔌 Run the Backend

Run the Spring Boot application in the backend folder with the `dev` profile. This will start the backend at
localhost on port `8080`. With the `dev` profile a local h2 in memory database will be used and the default admin
password will be set to `localTesting`.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 💻 Run the frontend

You will need [pnpm](https://pnpm.io/) to run the frontend. Other package managers should also work, but may cause
unexpected problems. To get the frontend up and running, follow the steps below. The frontend build tool used
is [vite](https://vite.dev/).

```bash
pnpm install
pnpm dev
```

## 🍻 Support Me

<a href="https://www.buymeacoffee.com/Snorzze" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>



