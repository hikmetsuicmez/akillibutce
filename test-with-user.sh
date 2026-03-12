#!/usr/bin/env bash
# ============================================================
# test-with-user.sh
# Belirli kullanici profiliyle Spring Boot testlerini calistirir.
#
# Kullanim:
#   bash test-with-user.sh PREMIUM    -> Premium kullanici testle
#   bash test-with-user.sh FREE       -> Ucretsiz kullanici testle
#   bash test-with-user.sh ALL        -> Tum profilleri sirayla test et
#
# Ornekler:
#   bash test-with-user.sh PREMIUM
#   bash test-with-user.sh FREE -Dtest=IslemServiceTest
# ============================================================

set -euo pipefail

# Renk kodlari
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$REPO_ROOT/backend"

# -------------------------------------------------------
# Parametre kontrolu
# -------------------------------------------------------
if [ $# -lt 1 ]; then
    echo -e "${RED}Hata: Kullanici profili belirtilmedi.${NC}"
    echo ""
    echo "Kullanim:"
    echo "  bash test-with-user.sh PREMIUM"
    echo "  bash test-with-user.sh FREE"
    echo "  bash test-with-user.sh ALL"
    echo ""
    echo "Opsiyonel ekstra Maven parametreleri:"
    echo "  bash test-with-user.sh PREMIUM -Dtest=TavsiyeServiceTest"
    exit 1
fi

PROFIL="${1^^}"  # Buyuk harfe cevir
shift            # Geri kalan parametreler Maven'a iletilecek
EKSTRA_PARAMS="$*"

# Profil dogrulama
case "$PROFIL" in
    PREMIUM|FREE|ALL)
        ;;
    *)
        echo -e "${RED}Hata: Gecersiz profil '$PROFIL'.${NC}"
        echo "Gecerli degerler: PREMIUM, FREE, ALL"
        exit 1
        ;;
esac

# -------------------------------------------------------
# Tek profil icin test calistirici fonksiyonu
# -------------------------------------------------------
profil_test_calistir() {
    local profil="$1"
    local spring_profil

    case "$profil" in
        PREMIUM)
            spring_profil="test-premium"
            ;;
        FREE)
            spring_profil="test-free"
            ;;
    esac

    echo ""
    echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║  Profil: $profil                          ${NC}"
    echo -e "${CYAN}║  Spring Profile: $spring_profil          ${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${BLUE}Ortam Degiskeni: TEST_USER_PROFIL=$profil${NC}"
    echo -e "${BLUE}Maven Profil: -Dspring.profiles.active=$spring_profil${NC}"
    if [ -n "$EKSTRA_PARAMS" ]; then
        echo -e "${BLUE}Ekstra Parametreler: $EKSTRA_PARAMS${NC}"
    fi
    echo ""

    START_TIME=$(date +%s)
    cd "$BACKEND_DIR"

    # TEST_USER_PROFIL ortam degiskenini set ederek Maven calistir
    if TEST_USER_PROFIL="$profil" \
       mvn test \
           -Dspring.profiles.active="$spring_profil" \
           --no-transfer-progress \
           $EKSTRA_PARAMS 2>&1; then

        END_TIME=$(date +%s)
        ELAPSED=$((END_TIME - START_TIME))
        echo ""
        echo -e "${GREEN}╔══════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║  [$profil] Testler BASARILI (${ELAPSED}s)     ${NC}"
        echo -e "${GREEN}╚══════════════════════════════════════════╝${NC}"
        cd "$REPO_ROOT"
        return 0
    else
        END_TIME=$(date +%s)
        ELAPSED=$((END_TIME - START_TIME))
        echo ""
        echo -e "${RED}╔══════════════════════════════════════════╗${NC}"
        echo -e "${RED}║  [$profil] Testler BASARISIZ (${ELAPSED}s)   ${NC}"
        echo -e "${RED}╚══════════════════════════════════════════╝${NC}"
        cd "$REPO_ROOT"
        return 1
    fi
}

# -------------------------------------------------------
# Ana mantik
# -------------------------------------------------------
if [ ! -f "$BACKEND_DIR/pom.xml" ]; then
    echo -e "${RED}Hata: $BACKEND_DIR/pom.xml bulunamadi.${NC}"
    exit 1
fi

GENEL_BASARISIZ=0

if [ "$PROFIL" = "ALL" ]; then
    echo -e "${YELLOW}Tum profiller sirayla test ediliyor...${NC}"
    for p in PREMIUM FREE; do
        profil_test_calistir "$p" || GENEL_BASARISIZ=1
    done
else
    profil_test_calistir "$PROFIL" || GENEL_BASARISIZ=1
fi

# -------------------------------------------------------
# Ozet
# -------------------------------------------------------
echo ""
if [ "$GENEL_BASARISIZ" -eq 0 ]; then
    echo -e "${GREEN}Tum testler basariyla tamamlandi.${NC}"
    exit 0
else
    echo -e "${RED}Bazi testler basarisiz oldu. Yukaridaki ciktilari inceleyin.${NC}"
    exit 1
fi
