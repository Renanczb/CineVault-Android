<div align="center">

# 🎬 CineVault - Android Nativo

**Aplicativo Android de gerenciamento de filmes pessoal, desenvolvido em Kotlin como parte de trabalho acadêmico de Desenvolvimento Mobile.**

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![TMDB](https://img.shields.io/badge/TMDB-01B4E4?style=for-the-badge&logo=themoviedatabase&logoColor=white)

</div>

---

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias Utilizadas](#%EF%B8%8F-tecnologias-utilizadas)
- [Funcionalidades](#-funcionalidades)
- [Telas do Aplicativo](#-telas-do-aplicativo)
- [Arquitetura e Estrutura](#-arquitetura-e-estrutura)
- [Como Executar o Projeto](#-como-executar-o-projeto)
- [Autores](#-autores)
- [Licença](#-licença)

---

## 📱 Sobre o Projeto

O **CineVault** é um aplicativo Android nativo desenvolvido em **Kotlin** que permite ao usuário organizar sua vida cinematográfica em um só lugar. Com ele é possível buscar filmes pelo título, consultar detalhes como sinopse, gênero, nota e duração, e salvar os títulos em listas personalizadas — Assistidos, Quero Assistir e Favoritos Privados.

Os dados são armazenados em tempo real no **Cloud Firestore** do Firebase, com autenticação gerenciada pelo **Firebase Authentication**. As informações dos filmes são obtidas diretamente da **API pública do TMDB** (The Movie Database). O acesso à lista de Favoritos Privados é protegido por **autenticação biométrica** (digital ou reconhecimento facial), garantindo privacidade ao usuário.

Este cliente é parte de um projeto que conta também com um cliente Cross-platform em React Native, ambos compartilhando a mesma base de dados no Firebase.

---

## 🛠️ Tecnologias Utilizadas

| Categoria | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| Plataforma | Android SDK (minSdk 26, targetSdk 34) |
| Arquitetura | MVVM (Model-View-ViewModel) |
| UI Components | Android Jetpack — Fragments, ViewBinding |
| Navegação | Jetpack Navigation Component |
| Estado | ViewModel + LiveData |
| Rede | Retrofit 2 + OkHttp + Gson Converter |
| Imagens | Glide |
| Backend | Firebase Authentication + Cloud Firestore |
| Biometria | AndroidX BiometricPrompt |
| API Externa | TMDB — The Movie Database (v3) |
| Async | Kotlin Coroutines |
| Build | Gradle (Kotlin DSL) |

---

## ✅ Funcionalidades

### 🔐 Autenticação
- Cadastro de usuários com nome, e-mail e senha via Firebase Authentication
- Login com e-mail e senha, com mensagens de erro específicas (e-mail não cadastrado, senha incorreta, e-mail já em uso)
- Logout com limpeza de sessão e redirecionamento automático para a tela de login
- Verificação de sessão ativa ao abrir o app — usuários logados vão direto à tela principal

### 🎬 Gerenciamento de Filmes
- Busca de filmes por título em tempo real via API do TMDB, com debounce de 400 ms para evitar requisições desnecessárias
- Exibição de detalhes completos: pôster, backdrop, sinopse, gênero, ano, duração e nota
- Adição de filmes às listas **Assistidos**, **Quero Assistir** ou **Favoritos Privados**
- Movimentação de filmes entre listas com diálogo de seleção
- Remoção de filmes com confirmação via diálogo
- Contadores de filmes por lista exibidos na tela inicial, atualizados a cada retorno à tela

### 🔒 Segurança
- Acesso à lista de Favoritos Privados protegido por **BiometricPrompt** (digital ou face)
- Fallback automático para dispositivos sem suporte a biometria
- Dados de cada usuário isolados no Firestore em `users/{uid}/{lista}`

### 🌐 Interface e Experiência do Usuário
- Suporte a dois idiomas: **Português (pt-BR)** como padrão e **Inglês (en-US)** como alternativo, alternável nas configurações
- Acessibilidade: `contentDescription` em todas as imagens, suporte a **TalkBack** e fontes que respeitam as configurações do sistema
- Tema escuro com paleta de cores consistente
- Indicadores de carregamento em todas as operações assíncronas
- Feedback de ações via Toasts e diálogos informativos

---

## 📸 Telas do Aplicativo

> 💡 *Adicione os screenshots abaixo após rodar o projeto no emulador ou dispositivo físico.*

| Login | Home | Busca |
|:---:|:---:|:---:|
| *screenshot* | *screenshot* | *screenshot* |

| Detalhes | Listas | Favoritos Privados |
|:---:|:---:|:---:|
| *screenshot* | *screenshot* | *screenshot* |

---

## 🗂️ Arquitetura e Estrutura

O projeto segue o padrão **MVVM** com separação clara de responsabilidades:

```
app/src/main/java/com/cinevault/
│
├── data/
│   ├── remote/
│   │   ├── api/          → RetrofitInstance (interceptor api_key) e interface TmdbApi
│   │   └── model/        → Modelos de resposta da API TMDB (MovieResult, MovieDetail, etc.)
│   └── local/
│       └── MovieEntity   → Modelo de dados persistido no Firestore
│
├── repository/
│   ├── AuthRepository    → Login, cadastro e logout com tratamento de erros Firebase
│   └── MovieRepository   → CRUD no Firestore + busca na API TMDB
│
├── viewmodel/
│   ├── AuthViewModel     → Gerencia estado de autenticação (UiState)
│   └── MovieViewModel    → Gerencia busca, listas, adição, remoção e movimentação
│
├── ui/
│   ├── auth/             → LoginFragment e RegisterFragment
│   ├── home/             → HomeFragment com contadores por lista
│   ├── search/           → SearchFragment + SearchAdapter
│   ├── details/          → DetailsFragment com informações completas do filme
│   ├── lists/            → ListsFragment + ListAdapter (Assistidos / Quero Assistir)
│   ├── private/          → PrivateFragment com autenticação biométrica
│   └── settings/         → SettingsFragment (idioma e logout)
│
└── utils/
    ├── UiState           → Sealed class com Loading, Success e Error
    └── Extensions        → Funções auxiliares (show/hide/toast)
```

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Android Studio Hedgehog](https://developer.android.com/studio) ou superior
- JDK 17
- Emulador Android configurado ou dispositivo físico com depuração USB ativada

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/cinevault-android.git
cd cinevault-android
```

### 2. Configurar o Firebase

1. Acesse o [Console do Firebase](https://console.firebase.google.com) e crie (ou selecione) um projeto
2. Clique em **Adicionar app > Android** e informe o pacote `com.cinevault`
3. Baixe o arquivo `google-services.json` gerado e salve em `app/google-services.json`
4. No console, ative o **Firebase Authentication** com o provedor **E-mail/senha**
5. Ative o **Cloud Firestore** e crie o banco no modo de teste ou produção

### 3. Configurar a API do TMDB

1. Crie uma conta gratuita em [themoviedb.org](https://www.themoviedb.org)
2. Acesse **Configurações > API** e solicite uma chave de desenvolvedor
3. Abra o arquivo `app/build.gradle` e substitua o placeholder pela sua chave:

```groovy
buildConfigField "String", "TMDB_API_KEY", "\"sua_chave_real_aqui\""
```

> A chave é enviada automaticamente como query parameter `api_key` em todas as requisições via OkHttp Interceptor, sem necessidade de alterações adicionais no código.

### 4. Sincronizar o Gradle

No Android Studio, clique em **File > Sync Project with Gradle Files** (ou aguarde a sincronização automática ao abrir o projeto).

### 5. Executar o aplicativo

Selecione um emulador ou dispositivo físico conectado e clique em **Run ▶** (ou use `Shift + F10`).

---

## 👥 Autores

| Nome | GitHub | E-mail |
|---|---|---|
| [Nome do Integrante 1] | [@usuario](https://github.com) | email@exemplo.com |
| [Nome do Integrante 2] | [@usuario](https://github.com) | email@exemplo.com |
| [Nome do Integrante 3] | [@usuario](https://github.com) | email@exemplo.com |
| [Nome do Integrante 4] | [@usuario](https://github.com) | email@exemplo.com |

---

## 📄 Licença

Este projeto foi desenvolvido exclusivamente para fins **acadêmicos** como parte da disciplina de Desenvolvimento Mobile. Seu uso, reprodução ou distribuição fora do contexto educacional deve ser previamente autorizado pelos autores.

---

<div align="center">
  <sub>Desenvolvido com ❤️ como Trabalho Final de Desenvolvimento Mobile</sub>
</div>
