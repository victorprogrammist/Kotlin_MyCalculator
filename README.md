# Kotlin_MyCalculator

Простенький калькулятор с формулами:

https://github.com/victorprogrammist/Kotlin_MyCalculator/blob/main/Screenshot.MyCalculator.jpg

https://github.com/victorprogrammist/Kotlin_MyCalculator/blob/main/Screenshot.MyCalculator.Help.jpg

=================================<br/>
Доступные операции:

1. Скобки

2. Операторы (по приоритетам)

1 : + сложение<br/>
1 : - вычитание<br/>
2 : * умножение<br/>
2 : / деление<br/>
2 : \% остаток от деления<br/>
3 : \^ возведение в степень<br/>
3 : log - логарифм: 10 log 1000 = 3<br/>
4 : ! факториал (гамма функция)<br/>

3. Модификатор очередности операндов.

Если перед оператором поставить апостроф (\')<br/>
то очередность его операторов будет наоборот.<br/>
Например 5 \'/ 1 = 0.2 (1/5)

=================================<br/>
Вот здесь основная логика расчета. Без комментариев.:<br/>
https://github.com/victorprogrammist/Kotlin_MyCalculator/blob/main/app/src/main/java/com/example/MyCalculator/evaluate.kt

Если кому-то вдруг захочется его запускать,<br/>
но никак компилить, то здесь apk под Android 10:<br/>
https://github.com/victorprogrammist/Kotlin_MyCalculator/tree/main/apk/release


