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

- Consultant Ebiznext
- _Data ingénieur_ & en charge du _pôle_ nantais
- Passionné de FP
- [@mmenestret](https://twitter.com/mmenestret)
- [geekocephale.com](http://geekocephale.com/blog/)

----

# Relation donnée / comportement: divergence de points de vue !



## Séparation donnée / comportement

La programmation orientée objet et la fonctionnelle ont des relations entre la donnée et leurs comportements fondamentalement opposées !



#### _Orienté objet_

L'OOP combine la donnée et le comportement dans des _classes_

![Pic pirate](/ressources/img/pic_pirate.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 300px; margin-bottom: 0px; margin-top: 0px;" -->

- Encapsule et cache la donnée
- Expose des méthodes pour agir dessus

```scala
case class Player(nickname: String, var level: Int) {
    def levelUp(): Unit          = { level = level + 1 }
    def sayHi(): String          = s"Hi, I'm player $nickname, I'm lvl $level !"
}
```



#### _Programmation fonctionnelle_

La FP sépare complètement la donnée et le comportement

![Pic pirate](/ressources/img/separation.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 300px; margin-bottom: 0px; margin-top: 0px;" -->

- La donnée est modelisée par les _types_ (ADTs)
- Le comportement est modélisé par des _fonctions_ (depuis et vers ces _types_)

```scala
case class Player(nickname: String, var level: Int)

object PlayerOperations {
    def levelUp(p: Player): Player = p.copy(level = p.level + 1)
    def sayHi(p: Player): String   = s"Hi, I'm player ${p.nickname}, I'm lvl ${p.level} !"
}
```



## Expression problem

Comment se comporte une code base existante quand on:

- Étends un type existant
- Étends les comportements d'un type existant



#### _Orienté objet_

- 👍 : Étendre un type existant
    - Nouvelle _classe_ qui _extends_ mon type existant (l'existant reste inchangé)
- 👎 : Étendre les comportements d'un type existant
    - Nouvelle _méthode_ sur l'interface (impact sur tous les sous types existant...)



#### _Programmation fonctionnelle_

- 👎  : Étendre un type existant
    - Nouvelle implémentation d'un sealed trait (impact sur toutes les fonctions existantes traitant ce type pour traiter ce nouveau cas...)
- 👍 : Étendre les comportements d'un type existant
    - Nouvelle fonction (l'existant reste inchangé)

----

# Polymorquoi ?



## Definition

Augmenter la réutilisation de code grâce à des constructions plus génériques.



## Des polymorphismes

- __Polymorphisme paramétrique__:
    - Une fonction se réfère à un symbole abstrait qui peut représenter n'importe quel type
    - `def reverse[A](as: List[A]): List[A]`
- __Polymorphisme d'héritage__:
    - Une fonction se réfère à plusieurs _classes_ en relation les unes aux autres à travers une _superclasse_ commune
- __Polymorphisme ad hoc__:
    - Une fonction se réfère à une _"interface"_ commune à un ensemble de types arbitraires qui abstrait une ou plusieurs propriétés communes

On va s'intéresser à ce dernier !



## Polymorphisme ad hoc

Une fonction se réfère à une _"interface"_ commune à un ensemble de types arbitraires qui abstrait une ou plusieurs propriétés communes:

- Evite de ré-implémenter une fonction pour chaques type concrets
- Le comportement d'une fonction dépend du type de son paramètre
- Implémentations
    - _Operator overloading_ (on en parlera pas ici)
    - __Interface subtyping / adapter pattern__
        - `def show(s: Showable): String`
        - ಥ_ಥ
    - __Type classes__
        - `def show[S](s: S)(implicit show: Showable[S]): String`
        - ᕕ( ᐛ )ᕗ

----

# Anatomie de la _type class_



## Anatomie comparée

- Groupe ou "__classe__" de types (_type_ *__class__*) qui partagent des propriétés communes
- Abstraction de ces propriétés
    - Ceux qui peuvent dire "bonjour"
    - Ceux qui ont des pétales
- Joue le même rôle que l'interface en OOP, __MAIS__:
    - Permet d'ajouter des propriétés à des types existant
    - Permet d'encoder un interfaçage conditionnel



## Anatomie fonctionnelle

En Scala, on encode les _type classes_: ce n'est pas une construction de première classe, c'est un design pattern:

1. Un _trait_ avec un paramètre de type qui expose le "contrat" de la _type class_
2. Des implémentations concrètes de ce trait



## Anatomie fonctionnelle


```scala
// Notre classe "métier"
case class Player(nickname: String, level: Int)
val geekocephale = Player("Geekocephale", 42)
```

```scala
// 1. Un trait: tous les T qui peuvent dire bonjour
trait CanGreet[T] {
    def sayHi(t: T): String
}

// 2. Une implémentation concrète pour que Player soit une instance de CanGreet
val playerGreeter: CanGreet[Player] = new CanGreet[Player] {
    def sayHi(t: Player): String = s"Hi, I'm player ${t.nickname}, I'm lvl ${t.level} !"
}
```

```scala
// Une fonction polymorphique
def greet[T](t: T, greeter: CanGreet[T]): String = greeter.sayHi(t)

// On l'utilise grâce à notre instance de type class
greet(geekocephale, playerGreeter)
```



## Anatomie fonctionnelle

`def greet[T](t: T, greeter: CanGreet[T]): String = greeter.sayHi(t)`

![seriously](/ressources/img/seriously.png) <!-- .element style="border: 0; background: None; box-shadow: None; width: 200px; margin-bottom: 0px; margin-top: 0px;" -->



## Anatomie fonctionnelle

- Utilisons les __implicits__ pour se rapprocher de ce qui est fait en _Haskell_
- 2 règles d'hygiène fondamentales:
    - Une seule instance de _type class_ par type
    - On ne met les instances de _type class_ que:
        - Dans l'object compagnon du trait de la _type class_
        - Dans l'object compagnon du type

```scala
object Player {
    implicit val playerGreeter: CanGreet[Player] = new CanGreet[Player] {
        def sayHi(t: Player): String = s"Hi, I'm player ${t.nickname}, I'm lvl ${t.level} !"
    }
}

def greet[T](t: T)(implicit greeter: CanGreet[T]): String = greeter.sayHi(t)
```



## Anatomie fonctionnelle

`greet(geekocephale)`

![so good](/ressources/img/so_good.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 200px; margin-bottom: 0px; margin-top: 0px;" -->



## Retour à l'anatomie comparée

#### Ajout de propriétés à des types existants

```scala
import java.net.URL

implicit val urlGreeter: CanGreet[URL] = new CanGreet[URL] {
    override def sayHi(t: URL): String = s"Hi, I'm an URL pointing at ${t.getPath}"
}
```

Maintenant votre _URL_ sait dire bonjour !

#### Interfaçage conditionnel

```scala
trait CanWave[A]

implicit def listGreeter[A: CanGreet: CanWave]: CanGreet[List[A]] = new CanGreet[List[A]] {
    override def sayHi(t: List[A]): String = s"Hi, I'm an List : [${t.map(implicitly[CanGreet[A]].sayHi).mkString(",")}]"
}
```

`List[A]` est une instance de la _type class_ `CanGreet` si et seulement si `A` en est une instance mais également une instance de `CanWave`.

----

# _Type class_ meca-augmentée !



## Context bound

```scala
def greet[T](t: T)(implicit greeter: CanGreet[T]): String = ???
```

Peut être refactoré en (absolument identique):

```scala
def greet[T: CanGreet](t: T): String = ???
```

Plus clean et exprime la contrainte que `T` doit être une instance de `CanGreet`



## _Type class_ _apply_

Mais comment récupère t-on notre greeter ?

```scala
def greet[T: CanGreet](t: T): String = {
    val greeter: CanGreet[T] = implicitly[CanGreet[T]]
    greeter.sayHi(t)
}
```

![seriously](/ressources/img/seriously.png) <!-- .element style="border: 0; background: None; box-shadow: None; width: 200px; margin-bottom: 0px; margin-top: 0px;" -->

```scala
object CanGreet {
    def apply[T](implicit C: CanGreet[T]): CanGreet[T] = C
}
```

... et maintenant ...

```scala
def greet[T: CanGreet](t: T): String = CanGreet[T].sayHi(t)
```

![so good](/ressources/img/so_good.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 200px; margin-bottom: 0px; margin-top: 0px;" -->



## _Type class_ _syntax_

On peut utiliser les _implicit class_ pour ajouter la _syntax_ de notre _type class_

```scala
implicit class CanGreetSyntax[T: CanGreet](t: T) {
    def greet: String = CanGreet[T].sayHi(t)
}
```

Ce qui nous permet:

```scala
geekocephale.greet
```

C'est important une bonne syntaxe !

![so good](/ressources/img/capello.jpg) <!-- .element style="border: 0; background: None; box-shadow: None; width: 400px; margin-bottom: 0px; margin-top: 0px;" -->



## Tous ensemble !

```scala
trait CanGreet[T] {
    def sayHi(t: T): String
}

object CanGreet {
    def apply[T](implicit C: CanGreet[T]): CanGreet[T] = C
}

implicit class CanGreetSyntax[T: CanGreet](t: T) {
    def greet: String = CanGreet[T].sayHi(t)
}

case class Player(nickname: String, var level: Int)

object Player {
    implicit val playerGreeter: CanGreet[Player] = new CanGreet[Player] {
        def sayHi(t: Player): String = s"Hi, I'm player ${t.nickname}, I'm lvl ${t.level} !"
    }
}
```

----

# La boite à outils



## Simulacrum

[Simulacrum](https://github.com/mpilquist/simulacrum) permet de se débarasser du boiler plate

```scala
import simulacrum._

@typeclass trait CanGreet[T] {
    @op("greet") def sayHi(t: T): String
}
```



## Magnolia

[Magnolia](https://github.com/propensive/magnolia) permet la dérivation automatique de _type classes_

#### Product types

```scala
type A
type B
case class C(a: A, b: B)
```

#### Sum types

```scala
sealed trait C
case class A() extends C
case class B() extends C
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
- L'_ad hoc polymorphism_ sans héritage / sub-typing d'interfaces
- D'ajouter du comportement à un type __à posteriori__ sans y toucher

----

# Merci !

___

#### [@mmenestret](https://twitter.com/mmenestret)

#### [geekocephale.com](http://geekocephale.com/blog/)
