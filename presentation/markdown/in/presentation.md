# Anatomie d'une _type class_

[![Typeclass anatomy](/ressources/img/sponge_bob.png)](https://www.deviantart.com/alessandroconti)
 <!-- .element style="border: 0; background: None; box-shadow: None; width: 800px; margin-bottom: 0px; margin-top: 0px;" -->

----

# Sommaire

- Moi, moi, moi !
- Relation donnée / comportement: divergence de points de vue
- Polymorquoi ?
- Anatomie de la _type class_
- _Type class_ meca-augmentée !
- La boite à outils
- Aller plus haut !

----

# Moi, moi, moi !

- _Data ingénieur_ __Ebiznext__
    - _ESN_ avec une forte expertise autour de la data / Scala
    - Animateur du pôle data
    - En charge de la _branche_ nantaise
- Co-organisateur du _SNUG_
- Passionné de _FP_
    - [@mmenestret](https://twitter.com/mmenestret)
    - [geekocephale.com](http://geekocephale.com/blog/)

----

# Relation donnée / comportement: divergence de points de vue !



## Séparation donnée / comportement

La programmation orientée objet et la programmation fonctionnelle: deux approches **opposées** !



### _Orienté objet_

![Pic pirate](/ressources/img/pic_pirate.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 300px; margin-bottom: 0px; margin-top: 0px;" -->

__L'OOP combine la donnée et les comportements au sein de _classes___

- Encapsule et __cache la donnée dans un état interne__
- Expose les comportements sous forme de méthodes pour celui-ci

```tut:silent
final class Player(private val name: String, private var level: Int) {
    def levelUp(): Unit          = { level = level + 1 }
    def sayHi(): String          = s"Hi, I'm player $name, I'm lvl $level !"
}
```



### _Programmation fonctionnelle_

![Pic pirate](/ressources/img/separation.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 300px; margin-bottom: 0px; margin-top: 0px;" -->

__La FP sépare complètement la donnée des comportements__

- La donnée est modelisée par des _types algébriques de donnée_ (ADTs)
- Les comportement sont modélisés par des _fonctions_ (depuis et vers ces _types_)

```tut:silent
final case class Player(name: String, level: Int)

object PlayerOperations {
    def levelUp(p: Player): Player = p.copy(level = p.level + 1)
    def sayHi(p: Player): String   = s"Hi, I'm player ${p.name}, I'm lvl ${p.level} !"
}
```



## Expression problem

Comment se comporte un langage ou un paradigme quand on:

- Étend un type existant (ajouter des "cas" à un type)
    - __Personnage__ = _Joueur_ + _NPC_
    - Et si on ajoute _Boss_ ?
- Étend les comportements d'un type existant
    - Un personnage peut __dire bonjour__ et __monter en niveau__
    - Et si on ajoute le comportement de __se déplacer__ ?

Ces actions entrainent-elles des modifications de la code base existante ?



### _Orienté objet_

- 👍 : __Étendre un type existant__
    - Juste une nouvelle _classe_ qui _extends_ mon type à étendre (la code base d'origine reste inchangée)
- 👎 : __Étendre les comportements d'un type existant__
    - Nouvelle _méthode_ sur le type dont on veut étendre le comportement
    - Impact sur tous ses sous types pour y implémenter cette nouvelle méthode...



### _Programmation fonctionnelle_

- 👎  : __Étendre un type existant__
    - Nouvelle implémentation du _trait_ représentant le type à étendre
    - Impact sur toutes les fonctions existantes prenant ce type en paramètre pour traiter cette nouvelle implémentation...
- 👍 : __Étendre les comportements d'un type existant__
    - Juste une nouvelle fonction (la code base d'origine reste inchangée)

----

# Polymorquoi ?



## Definition

Mécanisme visant à augmenter la réutilisation de code grâce à des constructions plus génériques.

__Il y a plusieurs types de polymorphisme.__



## Polymorphisme paramétrique

Une fonction se réfère à un symbole abstrait qui peut représenter n'importe quel type.

`def reverse[A](as: List[A]): List[A] = ???`



## Polymorphisme d'héritage

Plusieurs _classes_ héritent leurs comportements d'une _super classe_ commune.

```tut:silent
class Character(private val name: String) {
    def sayHi(): String = s"Hi, I'm $name"
}

class Player(private val name: String, private var level: Int) extends Character(name) {
    def levelUp(): Unit = { level = level + 1 }
}
```



## Polymorphisme ad hoc

- Une fonction se réfère à une _"interface"_ commune à un ensemble de types arbitraires
- Cette _"interface"_ abstrait un ou plusieurs comportements communs à ces types
- Evite de ré-implémenter une fonction pour chaque type concrets
    - Son comportement dépendra du type concret de son / ses paramètre(s)
- On va s'intéresser à celui-ci !



## Deux implémentations du polymorphisme ad hoc

- __Interface subtyping / adapter pattern__ - ಥ_ಥ
    - `def show(s: Showable): String`
- __Type classes__ - ᕕ( ᐛ )ᕗ
    - `def show[S: Showable](s: S): String`

----

# Anatomie de la _type class_



## Observations

- Construction introduite en _Haskell_ par **Philip Wadler**
- Représente un groupe ou une "__classe__" de types (_type_ *__class__*) qui partagent des propriétés communes
- Par exemple:
    - Le groupe de ceux qui peuvent dire "bonjour"
    - Le groupe de ceux qui ont des pétales



## Anatomie comparée

Joue le même rôle qu'une _interface_ en _OOP_, __MAIS__:

- Permet d'ajouter des propriétés à des types existant __à posteriori__
- Permet d'encoder une __interface conditionnelle__



## Anatomie fonctionnelle

En Scala, on __encode__ les _type classes_, ce n'est pas une construction de première classe du langage mais un design pattern (ce n'est pas le cas de tous les langages...). 

On l'implémente grâce à:

1. Un _trait_ avec un _paramètre de type_ qui expose les propriétés qui sont abstraites par la _type class_
2. Les implémentations concrètes de ce trait



## Etude d'un spécimen


```tut:silent
// Notre classe "métier"
final case class Player(name: String, level: Int)
val geekocephale = Player("Geekocephale", 42)
```
---
```tut:silent
// 1. Un trait: tous les T qui peuvent dire bonjour
trait CanSayHi[T] {
    def sayHi(t: T): String
}

// 2. Une implémentation concrète pour que Player soit une instance de CanSayHi
val playerGreeter: CanSayHi[Player] = new CanSayHi[Player] {
    def sayHi(t: Player): String = s"Hi, I'm player ${t.name}, I'm lvl ${t.level} !"
}
```

---

```tut:silent
// Une fonction polymorphique
def greet[T](t: T, greeter: CanSayHi[T]): String = greeter.sayHi(t)
```

```tut
greet(geekocephale, playerGreeter)
```



## Anatomie fonctionnelle

Utilisons les __implicits__ pour se rapprocher de ce qui est fait en _Haskell_

```tut:silent
implicit val playerGreeter: CanSayHi[Player] = new CanSayHi[Player] {
    def sayHi(t: Player): String = s"Hi, I'm player ${t.name}, I'm lvl ${t.level} !"
}

def greet[T](t: T)(implicit greeter: CanSayHi[T]): String = greeter.sayHi(t)
```

```tut
greet(geekocephale)
```



## Nota bene

2 règles d'hygiène fondamentales:

- Une seule instance d'une _type class_ par type
- On ne met les instances de _type class_ que:
    - Dans l'object compagnon de la _type class_
    - Dans l'object compagnon du type



## Retour à l'anatomie comparée

### Ajout de propriétés à des types existants

```tut:silent
import java.net.URL

implicit val urlGreeter: CanSayHi[URL] = new CanSayHi[URL] {
    override def sayHi(t: URL): String = s"Hi, I'm an URL pointing at ${t.getHost}"
}
```

```tut
greet(new URL("http://geekocephale.com"))
```

Maintenant votre _URL_ sait dire bonjour !



## Retour à l'anatomie comparée

### Interfaçage conditionnel

```tut:silent
trait CanSayItsName[A] {
    def sayMyName(a: A): String
}

implicit def greeter[A](implicit nameSayer: CanSayItsName[A]): CanSayHi[A] = new CanSayHi[A] {
    override def sayHi(a: A): String = s"Hi, I'm ${nameSayer.sayMyName(a)} !"
}
```

`A` est une instance de la _type class_ `CanSayHi` si et seulement si `A` est également une instance de `CanSayItsName`.



## Retour à l'anatomie comparée

### Interfaçage conditionnel

```tut:silent
final case class Guild(members: List[Player])

implicit def guildGreeter(implicit playerGreeter: CanSayHi[Player]): CanSayHi[Guild] = new CanSayHi[Guild] {
    override def sayHi(g: Guild): String = s"""Hi, we are ${g.members.map(p => playerGreeter.sayHi(p).mkString(","))}"""
}
```

`Guild` est une instance de la _type class_ `CanSayHi` si et seulement si `Player` en est une instance également.

----

# _Type class_ meca-augmentée !



## Context bound

`def greet[T](t: T)(implicit greeter: CanSayHi[T]): String = ???`

Peut être refactoré en (__absolument identique__):

`def greet[T: CanSayHi](t: T): String = ???`

Plus clean et exprime plus clairement la __contrainte__ que `T` doit être une instance de `CanSayHi`



## _Type class_ _apply_

Mais comment récupère t-on notre greeter ?

```tut:silent
def greet[T: CanSayHi](t: T): String = {
    val greeter: CanSayHi[T] = implicitly[CanSayHi[T]]
    greeter.sayHi(t)
}
```

... `implicitly[CanSayHi[T]]`... ![seriously](/ressources/img/seriously.png) <!-- .element style="border: 0; background: None; box-shadow: None; width: 50px; margin-bottom: 0px; margin-top: 0px;" -->

---

```tut:silent
object CanSayHi {
    def apply[T](implicit C: CanSayHi[T]): CanSayHi[T] = C
}

def greet[T: CanSayHi](t: T): String = CanSayHi[T].sayHi(t)
```

C'est mieux ! ![so good](/ressources/img/so_good.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 50px; margin-bottom: 0px; margin-top: 0px;" -->



## _Type class_ _syntax_

On peut utiliser les _implicit class_ pour ajouter la _syntax_ de notre _type class_

```tut:silent
implicit class CanSayHiSyntax[T: CanSayHi](t: T) {
    def greet: String = CanSayHi[T].sayHi(t)
}
```

Ce qui nous permet d'écrire: `geekocephale.greet`

C'est important une bonne syntaxe !

![so good](/ressources/img/capello.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 400px; margin-bottom: 0px; margin-top: 0px;" -->



## Tous ensemble !

```tut:reset:silent
trait CanSayHi[T] {
    def sayHi(t: T): String
}

object CanSayHi {
    def apply[T](implicit C: CanSayHi[T]): CanSayHi[T] = C
}

implicit class CanSayHiSyntax[T: CanSayHi](t: T) {
    def greet: String = CanSayHi[T].sayHi(t)
}
```

```tut:silent
final case class Player(name: String, var level: Int)

object Player {
    implicit val playerGreeter: CanSayHi[Player] = new CanSayHi[Player] {
        def sayHi(t: Player): String = s"Hi, I'm player ${t.name}, I'm lvl ${t.level} !"
    }
}
```

----

# La boite à outils



## Simulacrum

[Simulacrum](https://github.com/mpilquist/simulacrum) permet de se débarasser du boiler plate en le générant automatiquement, à la compilation, grâce à des macros

```scala
import simulacrum._

@typeclass trait CanSayHi[T] {
    @op("greet") def sayHi(t: T): String
}
```



## Magnolia

[Magnolia](https://github.com/propensive/magnolia) permet la dérivation automatique de _type classes_ pour les __ADTs__

__Product types:__

```tut:silent
type A
type B
final case class C(a: A, b: B)
```

__Sum types:__

```tut:silent
sealed trait C
final case class A() extends C
final case class B() extends C
```

Si `A` et `B` sont des instances d'une _type class_ `T`, alors `C` l'est aussi, "automatiquement" !

----

# Aller plus haut !

- [FP resources list](https://github.com/mmenestret/fp-ressources)
- [Anatomy of a type class](http://geekocephale.com/blog/2018/10/05/typeclasses)
- [Inheritance vs Generics vs TypeClasses in Scala](https://dev.to/jmcclell/inheritance-vs-generics-vs-typeclasses-in-scala-20op)
- [Mastering Typeclass Induction](https://www.youtube.com/watch?v=Nm4OIhjjA2o)
- [Type class, ultimate ad hoc](https://www.youtube.com/watch?v=2EdQFCP5mZ8)
- [Type classes in Scala](https://blog.scalac.io/2017/04/19/typeclasses-in-scala.html)
- [Implicits, type classes, and extension methods](https://kubuszok.com/compiled/implicits-type-classes-and-extension-methods/)

----

# Conclusion

Les _type classes_ permettent:

- De ne pas mixer comportements et donnée
- L'_ad hoc polymorphism_
- D'ajouter du comportement à un type __à posteriori__

----

# Merci !

___

#### [@mmenestret](https://twitter.com/mmenestret)

#### [geekocephale.com](http://geekocephale.com/blog/)