# 💰 FK_Balance

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Minecraft](https://img.shields.io/badge/Minecraft-Plugin-brightgreen?style=for-the-badge&logo=minecraft)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)

**FK_Balance** é um plugin de economia avançado para servidores de Minecraft. Diferente de sistemas simples, ele foi projetado para suportar **múltiplas moedas** (ex: Money e Cash) simultaneamente, utilizando armazenamento em banco de dados para garantir a integridade e escalabilidade dos dados dos jogadores.

## ✨ Funcionalidades

- **Multi-Moedas:** Gerencie diferentes economias no mesmo servidor de forma independente.
- **Persistência de Dados:** Integração nativa com **MySQL** para armazenamento via UUID.
- **Comandos Administrativos:** Controle total sobre saldos (Give, Set, Take).
- **Interação entre Jogadores:** Sistema de transferência (`/pay`) seguro.
- **Rankings:** Sistema de `/money top` para exibir os jogadores mais ricos.
- **Interface Intuitiva:** Suporte a menus (GUIs) para visualização de saldos e transações.

## 🛠️ Tecnologias e APIs

- **Java:** Linguagem principal do projeto.
- **Bukkit/Spigot/Paper API:** Interface de desenvolvimento para Minecraft.
- **MySQL / SQLite:** Motores de banco de dados suportados.
- **Maven:** Gerenciamento de dependências e build.

## 📦 Instalação e Configuração

1. Certifique-se de ter um servidor de banco de dados (MySQL) ativo.
2. Baixe o arquivo `.jar` na aba [Releases](https://github.com/Albiere-Lima/FK_Balance/releases).
3. Coloque o arquivo na pasta `/plugins` do seu servidor.
4. Reinicie o servidor para gerar o arquivo `config.yml`.
5. Insira as credenciais do seu banco de dados na configuração e use `/fkbalance reload`.

## 💻 Comandos Principais

| Comando | Descrição |

`<Balance>` - Usado para dizer o nome da balance nas configurações.

| :--- | :--- |

| `/<Balance>` | Visualiza o seu saldo atual em todas as moedas. |

| `/<Balance> pay <jogador> <quantia>` | Transfere valores para outro usuário. |

| `/<Balance> top` | Exibe o ranking dos jogadores mais ricos. |

| `/<Balance> give/set/take` | (Admin) Gerencia a economia do servidor. |


## 🚀 Como contribuir

Como este é um projeto em constante evolução (especialmente na integração de inventários paginados), sinta-se à vontade para:

1. Abrir uma **Issue** para relatar bugs.
2. Enviar um **Pull Request** com melhorias de código.
3. Sugerir novas integrações.

---
Desenvolvido por [Albiere](https://github.com/Albiere-Lima) 🚀
